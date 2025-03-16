package kz.bars.wellify.admin_service.utils;

import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class JWTUtils {

    /**
     * Получает текущий JWT токен пользователя из контекста безопасности.
     *
     * @return JWT токен, если пользователь аутентифицирован, иначе null.
     */
    public static Jwt getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof JwtAuthenticationToken) {
            return ((JwtAuthenticationToken) authentication).getToken();
        }
        log.warn("Couldn't extract user");

        return null;
    }

    /**
     * Получает идентификатор текущего пользователя (userId).
     *
     * @return Строка с userId (поле "sub" в JWT), если пользователь аутентифицирован, иначе null.
     */
    public static String getCurrentUserId() {
        Jwt jwt = getCurrentUser();
        if (jwt != null) {
            return jwt.getClaimAsString("sub"); // В Keycloak "sub" — это userId
        }
        return null;
    }

    /**
     * Получает имя пользователя (логин или username).
     *
     * @return Строка с username (поле "preferred_username" в JWT), если пользователь аутентифицирован, иначе null.
     */
    public static String getCurrentUserName() {
        Jwt jwt = getCurrentUser();
        if (jwt != null) {
            return jwt.getClaimAsString("preferred_username");
        }
        return null;
    }

    /**
     * Получает имя текущего пользователя.
     *
     * @return Строка с именем (поле "given_name" в JWT), если пользователь аутентифицирован, иначе null.
     */
    public static String getCurrentUserFirstName() {
        Jwt jwt = getCurrentUser();
        if (jwt != null) {
            return jwt.getClaimAsString("given_name");
        }
        return null;
    }

    /**
     * Получает фамилию текущего пользователя.
     *
     * @return Строка с фамилией (поле "family_name" в JWT), если пользователь аутентифицирован, иначе null.
     */
    public static String getCurrentUserLastName() {
        Jwt jwt = getCurrentUser();
        if (jwt != null) {
            return jwt.getClaimAsString("family_name");
        }
        return null;
    }

    /**
     * Получает email текущего пользователя.
     *
     * @return Строка с email (поле "email" в JWT), если пользователь аутентифицирован, иначе null.
     */
    public static String getCurrentUserEmail() {
        Jwt jwt = getCurrentUser();
        if (jwt != null) {
            return jwt.getClaimAsString("email");
        }
        return null;
    }
}
