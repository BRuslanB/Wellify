package kz.bars.wellify.admin_service.exception;

import org.springframework.http.HttpStatus;

public class RoleNotFoundException extends ApiException {
    public RoleNotFoundException(String roleName) {
        super("Role '" + roleName + "' not found", HttpStatus.NOT_FOUND);
    }
}
