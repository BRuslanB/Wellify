package kz.bars.wellify.admin_service.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import kz.bars.wellify.admin_service.dto.*;
import kz.bars.wellify.admin_service.config.JwtTokenProvider;
import kz.bars.wellify.admin_service.service.KeycloakService;
import kz.bars.wellify.admin_service.utils.UserUtils;
import lombok.RequiredArgsConstructor;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication API", description = "API for authentication")
public class AuthController {

    private final KeycloakService keycloakService;
    private final JwtTokenProvider jwtTokenProvider;

    // Эндпоинт для регистрации пользователя
    @PostMapping(value = "/sign-up")
    @Operation(summary = "Sign up")
    public ResponseEntity<UserResponseDto> signUp(@RequestBody UserCreateDto userCreateDto) {
        UserRepresentation createdUser = keycloakService.signUp(userCreateDto);
        UserResponseDto userResponseDto = new UserResponseDto(createdUser.getUsername(), createdUser.getEmail(),
                createdUser.getFirstName(), createdUser.getLastName());
        return ResponseEntity.status(HttpStatus.CREATED).body(userResponseDto);
    }

    // Эндпоинт для авторизации пользователя
    @PostMapping(value = "/sign-in")
    @Operation(summary = "Sign in")
    public ResponseEntity<String> signIn(@RequestBody UserSignInDto userSignInDto) {
        String token = keycloakService.signIn(userSignInDto);
        return ResponseEntity.ok(token);
    }

    // Эндпоинт для смены пароля (доступен для авторизированных пользоваталей)
    @PostMapping(value = "/change-password")
    @PreAuthorize("isAuthenticated")
    @Operation(summary = "Change password")
    public ResponseEntity<String> changePassword(@RequestBody UserChangePasswordDto userChangePasswordDto) {

        String currentUserName = UserUtils.getCurrentUserName();
        if (currentUserName == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Couldn't Identify User");
        }

        try {
            keycloakService.changePassword(currentUserName, userChangePasswordDto);
            return ResponseEntity.ok("Password changed!");

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error on changing password");
        }
    }

    // Эндпоинт выхода из приложения (доступен для авторизированных пользоваталей)
    @PostMapping("/logout")
    @Operation(summary = "Logout")
    @PreAuthorize("isAuthenticated")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        String token = jwtTokenProvider.resolveToken(request);
        keycloakService.logout(token);
        return ResponseEntity.ok("User logged out successfully.");
    }
}
