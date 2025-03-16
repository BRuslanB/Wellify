package kz.bars.wellify.admin_service.utils.impl;

import kz.bars.wellify.admin_service.model.Role;
import kz.bars.wellify.admin_service.model.User;
import kz.bars.wellify.admin_service.repository.RoleRepository;
import kz.bars.wellify.admin_service.repository.UserRepository;
import kz.bars.wellify.admin_service.utils.UserSynchronizationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class UserSynchronizationServiceImpl implements UserSynchronizationService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    /**
     * Синхронизировать пользователя из Keycloak с локальной базой данных.
     * Этот метод обновляет или создаёт запись пользователя, используя данные, полученные из токена.
     */
    @Transactional
    public void syncUserFromToken(String keycloakId, String username, String email, String firstName, String lastName,
                                  List<String> roles) {

        User user = userRepository.findById(keycloakId).orElseGet(() -> {
            User newUser = new User();
            newUser.setId(keycloakId);
            return newUser;
        });

        // Обновляем основные поля
        user.setUsername(username);
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setStatus(User.UserStatus.ACTIVE);

        // Синхронизация ролей: сопоставляем строки с локальными сущностями Role.
        Set<Role> roleEntities = roles.stream().map(roleName -> roleRepository.findByName(roleName)
                .orElseGet(() -> {
                    Role newRole = new Role();
                    newRole.setName(roleName);
                    newRole.setDescription("Автоматически созданная роль");
                    Role savedRole = roleRepository.save(newRole);
                    log.info("Created new role: {} with id: {}", savedRole.getName(), savedRole.getId());
                    return savedRole;
                })).collect(Collectors.toSet());
        user.setRoles(roleEntities);

        userRepository.save(user);
    }

    /**
     * Синхронизировать пользователя из Keycloak с локальной базой данных.
     * Этот метод обновляет или создаёт запись пользователя, используя данные, полученные напрямую из Keycloak.
     */
    @Transactional
    public void syncUserFromRepresentation(UserRepresentation kcUser, User.UserStatus status) {

        String keycloakId = kcUser.getId();

        // Найти пользователя в локальной БД по Keycloak ID или создать новый, если не найден
        User user = userRepository.findById(keycloakId)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setId(keycloakId);
                    return newUser;
                });

        // Обновляем основные поля
        user.setUsername(kcUser.getUsername());
        user.setEmail(kcUser.getEmail());
        user.setFirstName(kcUser.getFirstName());
        user.setLastName(kcUser.getLastName());
        user.setStatus(status);

        userRepository.save(user);
    }
}
