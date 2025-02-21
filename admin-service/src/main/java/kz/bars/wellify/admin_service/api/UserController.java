package kz.bars.wellify.admin_service.api;

import kz.bars.wellify.admin_service.dto.*;
import kz.bars.wellify.admin_service.service.KeycloakService;
import kz.bars.wellify.admin_service.utils.UserUtils;
import lombok.RequiredArgsConstructor;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/user")
@RequiredArgsConstructor
public class UserController {

    private final KeycloakService keycloakService;

    // Эндпоинт для вывода всех ролей (для теста)
    @GetMapping("/roles")
    public List<String> getUserRoles() {
        // Получаем объект аутентификации из SecurityContextHolder
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // Извлекаем и возвращаем список ролей (authorities)
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
    }

    // Эндпоинт для создания пользователя
    @PostMapping(value = "/create")
    public ResponseEntity<UserResponseDto> createUser(@RequestBody UserCreateDto userCreateDto) {
        UserRepresentation createdUser = keycloakService.createUser(userCreateDto);
        UserResponseDto userResponseDto = new UserResponseDto(createdUser.getUsername(), createdUser.getEmail(),
                createdUser.getFirstName(), createdUser.getLastName());
        return ResponseEntity.status(HttpStatus.CREATED).body(userResponseDto);
    }

    // Эндпоинт для авторизации пользователя
    @PostMapping(value = "/sign-in")
    public ResponseEntity<String> signIn(@RequestBody UserSignInDto userSignInDto) {
        String token = keycloakService.signIn(userSignInDto);
        return ResponseEntity.ok(token);
    }

    // Эндпоинт для смены пароля (доступен для авторизированных пользоваталей)
    @PostMapping(value = "/change-password")
    @PreAuthorize("isAuthenticated")
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

    // Эндпоинт для удаления пользователя (доступен только администратору)
    @PostMapping("/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteUser(@RequestBody UserBlockAndDeleteDto userDeleteDto) {
        try {
            keycloakService.deleteUser(userDeleteDto);
            return ResponseEntity.ok("User " + userDeleteDto.username + " has been deleted.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting user: " + e.getMessage());
        }
    }

    // Эндпоинт для блокировки пользователя (доступен только администратору)
    @PostMapping("/block")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> blockUser(@RequestBody UserBlockAndDeleteDto userBlockDto) {
        try {
            keycloakService.blockUser(userBlockDto);
            return ResponseEntity.ok("User " + userBlockDto.username + " has been blocked.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error blocking user: " + e.getMessage());
        }
    }

    // Эндпоинт для разблокировки пользователя (доступен только администратору)
    @PostMapping("/unblock")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> unblockUser(@RequestBody UserBlockAndDeleteDto userUnblockDto) {
        try {
            keycloakService.unblockUser(userUnblockDto);
            return ResponseEntity.ok("User " + userUnblockDto.username + " has been unblocked.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error unblocking user: " + e.getMessage());
        }
    }
}
