package kz.bars.wellify.admin_service.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserCreateDto {

    public String username;
    public String email;
    public String firstName;
    public String lastName;
    public String password;
}
