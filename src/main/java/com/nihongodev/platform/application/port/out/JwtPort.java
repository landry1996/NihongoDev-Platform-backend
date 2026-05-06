package com.nihongodev.platform.application.port.out;

import java.util.UUID;

public interface JwtPort {
    String generateAccessToken(UUID userId, String email, String role);
    String generateRefreshToken();
    UUID extractUserId(String token);
    String extractEmail(String token);
    boolean isTokenValid(String token);
    long getAccessTokenExpirationMs();
    long getRefreshTokenExpirationMs();
}
