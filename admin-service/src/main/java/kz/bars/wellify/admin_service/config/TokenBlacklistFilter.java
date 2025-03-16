package kz.bars.wellify.admin_service.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kz.bars.wellify.admin_service.model.User;
import kz.bars.wellify.admin_service.repository.UserRepository;
import kz.bars.wellify.admin_service.utils.TokenBlacklistService;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

public class TokenBlacklistFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final TokenBlacklistService tokenBlacklistService;
    private final UserRepository userRepository;

    public TokenBlacklistFilter(JwtTokenProvider jwtTokenProvider, TokenBlacklistService tokenBlacklistService, UserRepository userRepository) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.tokenBlacklistService = tokenBlacklistService;
        this.userRepository = userRepository;
    }

    /**
     * Фильтр для проверки JWT токенов и настройки контекста безопасности.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            // Получаем токен из заголовка запроса
            String token = jwtTokenProvider.resolveToken(request);

            if (token != null) {
                // Проверяем, находится ли токен в черном списке (Redis)
                if (tokenBlacklistService.isTokenBlacklisted(token)) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.getWriter().write("Token is blacklisted");
                    return;
                }
                // Проверяем статус пользователя по id в БД
                String userId = jwtTokenProvider.getUserId(token);
                Optional<User> userOpt = userRepository.findById(userId);

                if (userOpt.isPresent()) {
                    User user = userOpt.get();
                    User.UserStatus status = user.getStatus();
                    if (status == User.UserStatus.BLOCKED || status == User.UserStatus.DELETED) {
                        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                        response.getWriter().write("User is blocked or deleted");
                        return;
                    }
                }
            }
        } catch (Exception e) {
            throw new ServletException("Authentication failed", e);
        }

        // Передаем запрос следующему фильтру в цепочке
        filterChain.doFilter(request, response);
    }
}
