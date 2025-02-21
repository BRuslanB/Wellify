package kz.bars.wellify.admin_service.event;

import kz.bars.wellify.admin_service.service.KeycloakUserSynchronizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthenticationEventListener {

    private final KeycloakUserSynchronizationService keycloakUserSyncService;

    @EventListener
    public void onAuthenticationSuccess(AuthenticationSuccessEvent event) {
        if (event.getAuthentication() instanceof JwtAuthenticationToken jwtAuth) {
            // Здесь вызывается синхронизация по JWT при каждом успешном входе пользователя.
            keycloakUserSyncService.synchronizeUserFromToken(jwtAuth.getToken());
        }
    }
}
