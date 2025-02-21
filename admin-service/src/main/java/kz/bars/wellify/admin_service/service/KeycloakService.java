package kz.bars.wellify.admin_service.service;

import kz.bars.wellify.admin_service.dto.UserBlockAndDeleteDto;
import kz.bars.wellify.admin_service.dto.UserChangePasswordDto;
import kz.bars.wellify.admin_service.dto.UserCreateDto;
import kz.bars.wellify.admin_service.dto.UserSignInDto;
import org.keycloak.representations.idm.UserRepresentation;

public interface KeycloakService {

    UserRepresentation createUser(UserCreateDto user);
    String signIn(UserSignInDto userSignInDto);
    void changePassword(String username, UserChangePasswordDto userChangePasswordDto);
    void deleteUser(UserBlockAndDeleteDto userDeleteDto);
    void blockUser(UserBlockAndDeleteDto userBlockDto);
    void unblockUser(UserBlockAndDeleteDto userUnblockDto);
}
