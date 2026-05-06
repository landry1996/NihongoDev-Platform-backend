package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.port.in.LogoutPort;
import com.nihongodev.platform.application.port.out.RefreshTokenRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class LogoutUseCase implements LogoutPort {

    private final RefreshTokenRepositoryPort refreshTokenRepository;

    public LogoutUseCase(RefreshTokenRepositoryPort refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Override
    @Transactional
    public void logout(UUID userId) {
        refreshTokenRepository.deleteByUserId(userId);
    }
}
