package kz.bars.wellify.admin_service.service;

import kz.bars.wellify.admin_service.dto.UserProfileUpdateDto;
import kz.bars.wellify.admin_service.model.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

public interface UserService {

    User updateUserProfile(String userId, UserProfileUpdateDto userProfileUpdateDto);
    String uploadUserAvatarImage(String userId, MultipartFile file);
    byte[] getUserAvatarImage(String avatarUrl);
    Optional<User> getAuthenticatedUser();
}
