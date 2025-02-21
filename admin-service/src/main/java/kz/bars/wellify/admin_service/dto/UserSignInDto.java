package kz.bars.wellify.admin_service.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserSignInDto {

    public String username;
    public String password;
}
