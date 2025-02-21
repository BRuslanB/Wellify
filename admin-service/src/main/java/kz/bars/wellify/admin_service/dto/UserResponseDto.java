package kz.bars.wellify.admin_service.dto;

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
}
