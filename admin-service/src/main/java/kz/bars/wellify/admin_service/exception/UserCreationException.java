package kz.bars.wellify.admin_service.exception;

import org.springframework.http.HttpStatus;

public class UserCreationException extends ApiException {
    public UserCreationException() {
        super("Failed to create user", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
