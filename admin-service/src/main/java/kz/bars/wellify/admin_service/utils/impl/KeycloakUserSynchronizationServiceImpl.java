package kz.bars.wellify.admin_service.utils.impl;

import kz.bars.wellify.admin_service.utils.KeycloakUserSynchronizationService;
import kz.bars.wellify.admin_service.utils.UserSynchronizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KeycloakUserSynchronizationServiceImpl implements KeycloakUserSynchronizationService {

    private final UserSynchronizationService userSyncService;

    public void synchronizeUserFromToken(Jwt jwt) {

        // Извлекаем данные из токена
        String keycloakId = jwt.getSubject();
        String username = (String) jwt.getClaims().get("preferred_username");
        String email = (String) jwt.getClaims().get("email");
        String firstName = (String) jwt.getClaims().get("given_name");
        String lastName = (String) jwt.getClaims().get("family_name");

        // Извлекаем роли из токена с учетом фильтрации
        Map<String, Object> realmAccess = (Map<String, Object>) jwt.getClaims().get("realm_access");
        List<String> roles = (realmAccess != null && realmAccess.containsKey("roles"))
                ? (List<String>) realmAccess.get("roles")
                : Collections.emptyList();

        List<String> filteredRoles = roles.stream()
                .filter(role -> !role.startsWith("offline")
                        && !role.startsWith("default-roles")
                        && !role.startsWith("uma_authorization"))
                .collect(Collectors.toList());

        userSyncService.syncUserFromToken(keycloakId, username, email, firstName, lastName, filteredRoles);
    }
}
