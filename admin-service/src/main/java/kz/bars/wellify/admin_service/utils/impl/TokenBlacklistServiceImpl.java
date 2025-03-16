package kz.bars.wellify.admin_service.utils.impl;

import kz.bars.wellify.admin_service.utils.TokenBlacklistService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class TokenBlacklistServiceImpl implements TokenBlacklistService {

    private final RedisTemplate<String, String> tokenRedisTemplate; // Хранилище для недействительных токенов
    private static final String BLACKLIST_PREFIX = "blacklist::"; // Префикс ключа в Redis

    // Добавляем токен в черный список с TTL (время жизни = время жизни токена)
    public void blacklistToken(String token, long expirationTime) {

        tokenRedisTemplate.opsForValue().set(BLACKLIST_PREFIX + token, "invalid", expirationTime, TimeUnit.MILLISECONDS);
    }

    // Проверяем, есть ли токен в черном списке
    public boolean isTokenBlacklisted(String token) {

        return Boolean.TRUE.equals(tokenRedisTemplate.hasKey(BLACKLIST_PREFIX + token));
    }
}
