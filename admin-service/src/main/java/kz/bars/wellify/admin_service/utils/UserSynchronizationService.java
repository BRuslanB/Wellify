package kz.bars.wellify.admin_service.utils;

import kz.bars.wellify.admin_service.model.User;
import org.keycloak.representations.idm.UserRepresentation;

import java.util.List;

public interface UserSynchronizationService {

    void syncUserFromToken(String keycloakId, String username, String email, String firstName, String lastName,
                           List<String> roles);
    void syncUserFromRepresentation(UserRepresentation kcUser, User.UserStatus status);
}
