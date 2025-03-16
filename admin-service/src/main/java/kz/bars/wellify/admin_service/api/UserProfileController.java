package kz.bars.wellify.admin_service.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kz.bars.wellify.admin_service.dto.UserProfileUpdateDto;
import kz.bars.wellify.admin_service.dto.UserResponseDto;
import kz.bars.wellify.admin_service.model.User;
import kz.bars.wellify.admin_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLConnection;
import java.util.Optional;

@RestController
@RequestMapping("/user/profile")
@RequiredArgsConstructor
@Tag(name = "User Profile API", description = "API for managing user profile")
public class UserProfileController {

    private final UserService userService;

    /**
     * Обновление профиля пользователя.
     */
    @PatchMapping
    @PreAuthorize("isAuthenticated")
    @Operation(summary = "Update user profile")
    public ResponseEntity<UserResponseDto> updateUserProfile(@Valid @RequestBody UserProfileUpdateDto userProfileUpdateDto) {

        Optional<User> optionalUser = userService.getAuthenticatedUser();
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        User updatedUser = userService.updateUserProfile(optionalUser.get().getId(), userProfileUpdateDto);

        UserResponseDto userResponseDto = new UserResponseDto(updatedUser.getUsername(), updatedUser.getEmail(),
                updatedUser.getFirstName(), updatedUser.getLastName(), updatedUser.getPhone(), updatedUser.getAddress(),
                updatedUser.getProfileAvatarUrl(), updatedUser.getStatus());

        return ResponseEntity.ok(userResponseDto);
    }

    /**
     * Загрузка аватара пользователя в MinIO.
     */
    @PostMapping("/avatar")
    @PreAuthorize("isAuthenticated")
    @Operation(summary = "Upload user's avatar")
    public ResponseEntity<String> uploadUserAvatar(@RequestParam("file") MultipartFile file) {

        Optional<User> optionalUser = userService.getAuthenticatedUser();
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        String fileUrl = userService.uploadUserAvatarImage(optionalUser.get().getId(), file);
        return ResponseEntity.ok(fileUrl);
    }

    /**
     * Получение аватарки пользователя.
     */
    @GetMapping("/avatar")
    @PreAuthorize("isAuthenticated")
    @Operation(summary = "Get user's avatar URL")
    public ResponseEntity<byte[]> getUserAvatar() {

        Optional<User> optionalUser = userService.getAuthenticatedUser();
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        String avatarUrl = optionalUser.get().getProfileAvatarUrl();
        if (avatarUrl == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // Определяем MIME-тип
        String contentType = URLConnection.guessContentTypeFromName(avatarUrl);
        if (contentType == null) {
            contentType = "application/octet-stream"; // Файл неизвестного типа
        }

        byte[] avatarImage = userService.getUserAvatarImage(avatarUrl);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(avatarImage);
    }
}
