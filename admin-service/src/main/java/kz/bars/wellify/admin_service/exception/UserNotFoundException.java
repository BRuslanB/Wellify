package kz.bars.wellify.admin_service.exception;

import org.springframework.http.HttpStatus;

public class UserNotFoundException extends ApiException {

    public UserNotFoundException(String username) {
        super("User not found: " + username, HttpStatus.NOT_FOUND);
    }
}
