package kz.bars.wellify.admin_service.config;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@AllArgsConstructor
@NoArgsConstructor
@ConfigurationProperties(prefix = "keycloak")
public class KeycloakProperties {
    private String url;
    private String realm;
    private Admin admin;  // вложенное свойство для админских настроек
    private String publicKey;

    @Data
    public static class Admin {
        private String username;
        private String password;
        private String clientId;
        private String clientSecret;
        private String grantType;
    }
}
