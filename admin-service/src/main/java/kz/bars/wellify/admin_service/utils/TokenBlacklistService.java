package kz.bars.wellify.admin_service.utils;

public interface TokenBlacklistService {

    void blacklistToken(String token, long expirationTime);
    boolean isTokenBlacklisted(String token);
}
