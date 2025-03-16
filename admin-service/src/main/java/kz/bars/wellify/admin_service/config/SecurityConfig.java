package kz.bars.wellify.admin_service.config;

import kz.bars.wellify.admin_service.repository.UserRepository;
import kz.bars.wellify.admin_service.utils.TokenBlacklistService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
    private String jwkSetUri;

    private final JwtTokenProvider jwtTokenProvider;
    private final TokenBlacklistService tokenBlacklistService;
    private final UserRepository userRepository;

    public SecurityConfig(JwtTokenProvider jwtTokenProvider, TokenBlacklistService tokenBlacklistService, UserRepository userRepository) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.tokenBlacklistService = tokenBlacklistService;
        this.userRepository = userRepository;
    }

    /**
     * Настраивает цепочку фильтров безопасности.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        // Создаем JwtAuthenticationConverter и устанавливаем свой конвертер ролей
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(new KeycloakRoleConverter());

        http
                .csrf(AbstractHttpConfigurer::disable) // Отключение CSRF
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Настройка сессий
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.POST,"/auth/sign-up", "/auth/sign-in").permitAll() // Разрешение для регистрации и авторизации
                        .requestMatchers(HttpMethod.GET, "/api-docs/**", "/swagger-ui/**").permitAll()  // Разрешение для Swagger
                        .requestMatchers(HttpMethod.GET, "/info", "/healthcheck", "/metrics").permitAll() // Разрешение для Actuator
                        .anyRequest().authenticated() // Все остальные запросы требуют аутентификации
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .jwkSetUri(jwkSetUri)
                                .jwtAuthenticationConverter(jwtAuthenticationConverter)
                        )
                )
               // Добавляем фильтр перед стандартным фильтром, через новый экземпляр класса TokenBlacklistFilter
                .addFilterAfter(new TokenBlacklistFilter(jwtTokenProvider, tokenBlacklistService, userRepository),
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
