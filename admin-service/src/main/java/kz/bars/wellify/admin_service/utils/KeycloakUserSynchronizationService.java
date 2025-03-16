package kz.bars.wellify.admin_service.utils;

import org.springframework.security.oauth2.jwt.Jwt;

public interface KeycloakUserSynchronizationService {

    void synchronizeUserFromToken(Jwt jwt);
}
