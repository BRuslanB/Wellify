package kz.bars.wellify.admin_service.service.impl;

import io.minio.*;
import jakarta.transaction.Transactional;
import kz.bars.wellify.admin_service.config.KeycloakProperties;
import kz.bars.wellify.admin_service.dto.UserProfileUpdateDto;
import kz.bars.wellify.admin_service.model.User;
import kz.bars.wellify.admin_service.repository.UserRepository;
import kz.bars.wellify.admin_service.service.UserService;
import kz.bars.wellify.admin_service.utils.JWTUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.net.URL;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Log4j2
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final MinioClient minioClient;
    private final Keycloak keycloak;
    private final KeycloakProperties keycloakProperties;

    @Value("${minio.url}")
    private String minioUrl; // Url сервера MinIO

    @Value("${minio.bucket}")
    private String minioBucket; // Название бакета MinIO

    /**
     * Обновление профиля пользователя.
     */
    @Transactional
    public User updateUserProfile(String userId, UserProfileUpdateDto userProfileUpdateDto) {

        UserRepresentation keycloakUser = keycloak
                .realm(keycloakProperties.getRealm())
                .users()
                .get(userId) // Для поиска используем keycloak id
                .toRepresentation();

        if (keycloakUser == null) {
            log.error("User not found to change password");
            throw new IllegalArgumentException("User not found to update profile");
        }

        User localUser = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found to update profile"));

        // Проверка полей на null
        if (userProfileUpdateDto.getEmail() != null) {
            localUser.setEmail(userProfileUpdateDto.getEmail());
            keycloakUser.setEmail(userProfileUpdateDto.getEmail());
        }
        if (userProfileUpdateDto.getFirstName() != null) {
            localUser.setFirstName(userProfileUpdateDto.getFirstName());
            keycloakUser.setFirstName(userProfileUpdateDto.getFirstName());
        }
        if (userProfileUpdateDto.getLastName() != null) {
            localUser.setLastName(userProfileUpdateDto.getLastName());
            keycloakUser.setLastName(userProfileUpdateDto.getLastName());
        }
        if (userProfileUpdateDto.getPhone() != null) {
            localUser.setPhone(userProfileUpdateDto.getPhone());
        }
        if (userProfileUpdateDto.getAddress() != null) {
            localUser.setAddress(userProfileUpdateDto.getAddress());
        }

        keycloak.realm(keycloakProperties.getRealm())
                .users()
                .get(keycloakUser.getId())
                .update(keycloakUser);

        return userRepository.save(localUser);
    }

    /**
     * Загрузка аватара в MinIO.
     */
    @Transactional
    public String uploadUserAvatarImage(String userId, MultipartFile file) {

        try {
            // Проверяем, существует ли бакет
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(minioBucket).build());
            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(minioBucket).build());
            }

            // Валидация размера (не более 5 МБ)
            long maxFileSize = 5 * 1024 * 1024; // 5MB
            if (file.getSize() > maxFileSize) {
                throw new MaxUploadSizeExceededException(maxFileSize);
            }

            // Валидация типа файла (только изображение)
            List<String> allowedTypes = List.of(MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE, "image/webp");
            if (!allowedTypes.contains(file.getContentType())) {
                throw new IllegalArgumentException("Invalid file type. Allowed: JPEG, PNG, WEBP");
            }

            // Генерируем уникальное имя файла
            String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();

            // Получаем текущего пользователя
            User localUser = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            // Удаляем старый аватар, если он есть
            if (localUser.getProfileAvatarUrl() != null) {
                deleteOldProfilePicture(localUser.getProfileAvatarUrl());
            }

            // Загружаем новый аватар в MinIO
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(minioBucket)
                            .object(fileName)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );

            // Сохраняем ссылку в профиле пользователя
            localUser.setProfileAvatarUrl(fileName);
            userRepository.save(localUser);

            return fileName;

        } catch (Exception e) {
            throw new RuntimeException("Error uploading file to MinIO", e);
        }
    }

    /**
     * Получение аватарки пользователя из MinIO.
     */
    @Transactional
    public byte[] getUserAvatarImage(String avatarUrl) {

        try {
            InputStream avatarStream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(minioBucket)
                            .object(avatarUrl)
                            .build()
            );

            byte[] imageBytes = avatarStream.readAllBytes();
            avatarStream.close();

            return imageBytes;

        } catch (Exception e) {
            throw new RuntimeException("Error retrieving avatar from MinIO", e);
        }
    }

    /**
     * Получение текущего аутентифицированного пользователя.
     */
    public Optional<User> getAuthenticatedUser() {

        String userId = JWTUtils.getCurrentUserId();

        if (userId == null) {
            return Optional.empty();
        }
        return userRepository.findByIdAndStatus(userId, User.UserStatus.ACTIVE);
    }

    /**
     * Удаление старого аватара в MinIO.
     */
    private void deleteOldProfilePicture(String profilePictureUrl) {

        try {
            // Извлекаем имя файла из URL
            String fileName = Paths.get(new URL(profilePictureUrl).getPath()).getFileName().toString();

            // Удаляем объект из MinIO
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(minioBucket)
                            .object(fileName)
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("Error deleting old profile picture", e);
        }
    }
}
