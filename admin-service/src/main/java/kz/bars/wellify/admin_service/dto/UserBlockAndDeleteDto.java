package kz.bars.wellify.admin_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserBlockAndDeleteDto {

    @NotBlank
    public String username;
}
