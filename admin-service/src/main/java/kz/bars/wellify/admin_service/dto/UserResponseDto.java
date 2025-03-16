package kz.bars.wellify.admin_service.dto;

import kz.bars.wellify.admin_service.model.User;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDto {

    public String username;

    public String email;

    public String firstName;

    public String lastName;

    public String phone;

    public String address;

    public String profileAvatarUrl;

    public User.UserStatus status;
}
