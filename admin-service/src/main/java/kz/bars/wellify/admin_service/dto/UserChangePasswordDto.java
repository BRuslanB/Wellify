package kz.bars.wellify.admin_service.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserChangePasswordDto {

    public String newPassword;
}
