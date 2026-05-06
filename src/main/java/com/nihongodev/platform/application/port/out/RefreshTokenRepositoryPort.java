package com.nihongodev.platform.application.port.out;

import com.nihongodev.platform.domain.model.RefreshToken;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepositoryPort {
    RefreshToken save(RefreshToken token);
    Optional<RefreshToken> findByToken(String token);
    void deleteByUserId(UUID userId);
    void deleteByToken(String token);
}
