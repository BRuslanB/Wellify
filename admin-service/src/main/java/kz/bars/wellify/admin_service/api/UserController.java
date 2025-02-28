package kz.bars.wellify.admin_service.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kz.bars.wellify.admin_service.dto.UserBlockAndDeleteDto;
import kz.bars.wellify.admin_service.service.KeycloakService;
import lombok.RequiredArgsConstructor;
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
@Tag(name = "User API", description = "API for managing users")
public class UserController {

    private final KeycloakService keycloakService;

    // Эндпоинт для вывода всех ролей (для теста)
    @GetMapping("/roles")
    @Operation(summary = "Get user roles")
    public List<String> getUserRoles() {
        // Получаем объект аутентификации из SecurityContextHolder
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // Извлекаем и возвращаем список ролей (authorities)
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
    }

    // Эндпоинт для удаления пользователя (доступен только администратору)
    @PostMapping("/delete")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete user")
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
    @Operation(summary = "Block user")
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
    @Operation(summary = "Unblock user")
    public ResponseEntity<String> unblockUser(@RequestBody UserBlockAndDeleteDto userUnblockDto) {
        try {
            keycloakService.unblockUser(userUnblockDto);
            return ResponseEntity.ok("User " + userUnblockDto.username + " has been unblocked.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error unblocking user: " + e.getMessage());
        }
    }
}
