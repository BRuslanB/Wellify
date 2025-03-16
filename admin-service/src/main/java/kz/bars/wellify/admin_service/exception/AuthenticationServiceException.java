package kz.bars.wellify.admin_service.exception;

import org.springframework.http.HttpStatus;

public class AuthenticationServiceException extends ApiException {

    public AuthenticationServiceException() {
        super("Authentication service is unavailable", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public AuthenticationServiceException(String message) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
