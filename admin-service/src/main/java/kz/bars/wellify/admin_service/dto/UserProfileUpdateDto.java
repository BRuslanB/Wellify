package kz.bars.wellify.admin_service.dto;

import jakarta.validation.constraints.Email;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileUpdateDto {

    @Email
    public String email; // Разрешаем пользователю менять email

    public String firstName; // Разрешаем пользователю менять имя

    public String lastName; // Разрешаем пользователю менять фамилию

    public String phone;

    public String address;
}
