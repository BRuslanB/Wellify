package kz.bars.wellify.admin_service.service.impl;

import jakarta.ws.rs.core.Response;
import kz.bars.wellify.admin_service.config.KeycloakProperties;
import kz.bars.wellify.admin_service.dto.UserBlockAndDeleteDto;
import kz.bars.wellify.admin_service.dto.UserChangePasswordDto;
import kz.bars.wellify.admin_service.dto.UserCreateDto;
import kz.bars.wellify.admin_service.dto.UserSignInDto;
import kz.bars.wellify.admin_service.exception.*;
import kz.bars.wellify.admin_service.model.Role;
import kz.bars.wellify.admin_service.model.User;
import kz.bars.wellify.admin_service.repository.UserRepository;
import kz.bars.wellify.admin_service.service.KeycloakService;
import kz.bars.wellify.admin_service.service.UserSynchronizationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Log4j2
public class KeycloakServiceImpl implements KeycloakService {

    private final Keycloak keycloak;
    private final RestTemplate restTemplate;
    private final KeycloakProperties keycloakProperties;
    private final UserRepository userRepository;
    private final UserSynchronizationService userSynchronizationService;

    // Создание пользователя
    @Override
    public UserRepresentation createUser(UserCreateDto userCreateDto) {

        // Создание нового объекта пользователя
        UserRepresentation newUser = new UserRepresentation();
        newUser.setEmail(userCreateDto.email);
        newUser.setEmailVerified(true);
        newUser.setUsername(userCreateDto.username);
        newUser.setFirstName(userCreateDto.firstName);
        newUser.setLastName(userCreateDto.lastName);
        newUser.setEnabled(true);

        // Настройка учетных данных пользователя
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(userCreateDto.password);
        credential.setTemporary(false);
        newUser.setCredentials(List.of(credential));

        // Создаем пользователя через Keycloak Admin Client
        Response response = keycloak
                .realm(keycloakProperties.getRealm())
                .users()
                .create(newUser);

        if (response.getStatus() != HttpStatus.CREATED.value()) { // Ожидается статус 201
            log.error("Error on creating user, status: {}", response.getStatus());
            throw new UserCreationException();
        }

        // Поиск созданного пользователя по username
        List<UserRepresentation> searchUsers = keycloak.realm(keycloakProperties.getRealm())
                .users().search(userCreateDto.username, true); // Для поиска по полному совпадению используем парметр - true
        if (searchUsers.isEmpty()) {
            throw new UserNotFoundException(userCreateDto.username);
        }
        UserRepresentation createdUser = searchUsers.get(0);

        // Назначаем роль по умолчанию
        // Предполагается, что в Keycloak уже создана роль с именем по умолчанию
        List<RoleRepresentation> roles = keycloak.realm(keycloakProperties.getRealm())
                .roles()
                .list();

        Optional<RoleRepresentation> defaultRoleOpt = roles.stream()
                .filter(role -> String.valueOf(Role.RoleName.USER).equalsIgnoreCase(role.getName()))
                .findFirst();

        if (defaultRoleOpt.isEmpty()) {
            throw new RoleNotFoundException(String.valueOf(Role.RoleName.USER));
        }

        RoleRepresentation defaultRole = defaultRoleOpt.get();

        // Назначаем роль через ресурс realm-level role mappings:
        keycloak.realm(keycloakProperties.getRealm())
                .users()
                .get(createdUser.getId())
                .roles()       // Получаем RoleMappingResource
                .realmLevel()  // Получаем ресурс для realm-ролей
                .add(Collections.singletonList(defaultRole)); // Добавляем роль

        // Получаем актуальное представление пользователя из Keycloak
        UserRepresentation updatedUser = keycloak.realm(keycloakProperties.getRealm())
                .users().get(createdUser.getId()).toRepresentation();

        // Синхронизируем локальные данные (запись в таблицу users и roles)
        userSynchronizationService.syncUserFromRepresentation(updatedUser, User.UserStatus.ACTIVE);

        return updatedUser;
    }

    // Авторизация пользавателя
    @Override
    public String signIn(UserSignInDto userSignInDto) {
        String tokenEndpoint = keycloakProperties.getUrl() + "/realms/" + keycloakProperties.getRealm() + "/protocol/openid-connect/token";

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", keycloakProperties.getAdmin().getGrantType());
        formData.add("client_id", keycloakProperties.getAdmin().getClientId());
        formData.add("client_secret", keycloakProperties.getAdmin().getClientSecret());
        formData.add("username", userSignInDto.username);
        formData.add("password", userSignInDto.password);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded");

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(tokenEndpoint, new HttpEntity<>(formData, headers), Map.class);
            Map<String, Object> responseBody = response.getBody();

            if (!response.getStatusCode().is2xxSuccessful() || responseBody == null) {
                throw new InvalidCredentialsException();
            }

            return (String) responseBody.get("access_token");

        } catch (HttpClientErrorException.BadRequest ex) {
            throw new InvalidCredentialsException(); // Обрабатываем 400 Bad Request (неверный пароль)
        } catch (HttpClientErrorException.Unauthorized ex) {
            throw new InvalidCredentialsException(); // Обрабатываем 401 Unauthorized
        } catch (HttpStatusCodeException ex) {
            throw new AuthenticationServiceException("External auth service error: " + ex.getStatusCode());
        } catch (Exception ex) {
            throw new AuthenticationServiceException();
        }
    }

    // Изменение пароля
    @Override
    public void changePassword(String username, UserChangePasswordDto userChangePasswordDto) {

        List<UserRepresentation> users = keycloak
                .realm(keycloakProperties.getRealm())
                .users()
                .search(username, true); // Для поиска по полному совпадению используем парметр - true

        if (users.isEmpty()) {
            log.error("User not found to change");
            throw new UserNotFoundException(username);
        }

        UserRepresentation userRepresentation = users.get(0);
        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setType(CredentialRepresentation.PASSWORD);
        credentialRepresentation.setValue(userChangePasswordDto.newPassword);
        credentialRepresentation.setTemporary(false);

        keycloak.realm(keycloakProperties.getRealm())
                .users()
                .get(userRepresentation.getId())
                .resetPassword(credentialRepresentation);

        log.info("Password changed!");
    }

    // Удаление пользователя
    @Override
    public void deleteUser(UserBlockAndDeleteDto userDeleteDto) {

        List<UserRepresentation> users = keycloak
                .realm(keycloakProperties.getRealm())
                .users()
                .search(userDeleteDto.username, true); // Для поиска по полному совпадению используем парметр - true

        if (users.isEmpty()) {
            log.error("User not found to delete");
            throw new UserNotFoundException(userDeleteDto.username);
        }

        String userId = users.get(0).getId();
        keycloak.realm(keycloakProperties.getRealm())
                .users()
                .get(userId)
                .remove();

        // Найти пользователя в локальной БД по Keycloak ID
        User user = userRepository.findById(userId).orElse(null);

        // Помечаем локальную запись как уаленную (мякгое удаление)
        if (user != null) {
            user.setStatus(User.UserStatus.DELETED);
            userRepository.save(user);
        }

        log.info("User {} has been deleted", userDeleteDto.username);
    }

    // Блокировка пользователя
    @Override
    public void blockUser(UserBlockAndDeleteDto userBlockDto) {

        List<UserRepresentation> users = keycloak
                .realm(keycloakProperties.getRealm())
                .users()
                .search(userBlockDto.username, true); // Для поиска по полному совпадению используем парметр - true

        if (users.isEmpty()) {
            log.error("User not found to block");
            throw new UserNotFoundException(userBlockDto.username);
        }

        UserRepresentation userRepresentation = users.get(0);
        userRepresentation.setEnabled(false); // Отключение доступности пользователя

        keycloak.realm(keycloakProperties.getRealm())
                .users()
                .get(userRepresentation.getId())
                .update(userRepresentation);

        // Получаем актуальное представление пользователя из Keycloak
        UserRepresentation updatedUser = keycloak.realm(keycloakProperties.getRealm())
                .users().get(userRepresentation.getId()).toRepresentation();

        // Синхронизируем локальные данные (запись в таблицу users и roles)
        userSynchronizationService.syncUserFromRepresentation(updatedUser, User.UserStatus.BLOCKED);

        log.info("User {} has been blocked", userBlockDto.username);
    }

    // Разблокировка пользователя
    @Override
    public void unblockUser(UserBlockAndDeleteDto userUnblockDto) {

        List<UserRepresentation> users = keycloak
                .realm(keycloakProperties.getRealm())
                .users()
                .search(userUnblockDto.username, true); // Для поиска по полному совпадению используем парметр - true

        if (users.isEmpty()) {
            log.error("User not found to unblock");
            throw new UserNotFoundException(userUnblockDto.username);
        }

        UserRepresentation userRepresentation = users.get(0);
        userRepresentation.setEnabled(true); // Включение доступности пользователя

        keycloak.realm(keycloakProperties.getRealm())
                .users()
                .get(userRepresentation.getId())
                .update(userRepresentation);

        // Получаем актуальное представление пользователя из Keycloak
        UserRepresentation updatedUser = keycloak.realm(keycloakProperties.getRealm())
                .users().get(userRepresentation.getId()).toRepresentation();

        // Синхронизируем локальные данные (запись в таблицу users и roles)
        userSynchronizationService.syncUserFromRepresentation(updatedUser, User.UserStatus.ACTIVE);

        log.info("User {} has been unblocked", userUnblockDto.username);
    }
}
