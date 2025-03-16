package kz.bars.wellify.admin_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserCreateDto {

    @NotBlank
    public String username;

    @NotBlank
    @Email
    public String email;

    @NotBlank
    public String firstName;

    @NotBlank
    public String lastName;

    public String phone;

    public String address;

    @NotBlank
    public String password;
}
