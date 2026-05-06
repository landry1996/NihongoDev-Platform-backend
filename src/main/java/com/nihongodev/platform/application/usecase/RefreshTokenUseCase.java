package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.dto.AuthResponse;
import com.nihongodev.platform.application.dto.UserDto;
import com.nihongodev.platform.application.port.in.RefreshTokenPort;
import com.nihongodev.platform.application.port.out.JwtPort;
import com.nihongodev.platform.application.port.out.RefreshTokenRepositoryPort;
import com.nihongodev.platform.application.port.out.UserRepositoryPort;
import com.nihongodev.platform.domain.exception.UnauthorizedException;
import com.nihongodev.platform.domain.model.RefreshToken;
import com.nihongodev.platform.domain.model.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RefreshTokenUseCase implements RefreshTokenPort {

    private final RefreshTokenRepositoryPort refreshTokenRepository;
    private final UserRepositoryPort userRepository;
    private final JwtPort jwtPort;

    public RefreshTokenUseCase(RefreshTokenRepositoryPort refreshTokenRepository,
                               UserRepositoryPort userRepository,
                               JwtPort jwtPort) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
        this.jwtPort = jwtPort;
    }

    @Override
    @Transactional
    public AuthResponse refresh(String refreshTokenValue) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenValue)
                .orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));

        if (refreshToken.isExpired()) {
            refreshTokenRepository.deleteByToken(refreshTokenValue);
            throw new UnauthorizedException("Refresh token expired");
        }

        User user = userRepository.findById(refreshToken.getUserId())
                .orElseThrow(() -> new UnauthorizedException("User not found"));

        refreshTokenRepository.deleteByUserId(user.getId());

        String newAccessToken = jwtPort.generateAccessToken(user.getId(), user.getEmail(), user.getRole().name());
        String newRefreshTokenValue = jwtPort.generateRefreshToken();

        RefreshToken newRefreshToken = RefreshToken.create(user.getId(), newRefreshTokenValue, jwtPort.getRefreshTokenExpirationMs());
        refreshTokenRepository.save(newRefreshToken);

        UserDto userDto = new UserDto(
                user.getId(), user.getFirstName(), user.getLastName(), user.getEmail(),
                user.getRole(), user.getJapaneseLevel(), user.getObjective(),
                user.getAvatarUrl(), user.isActive(), user.getCreatedAt()
        );

        return AuthResponse.of(newAccessToken, newRefreshTokenValue, jwtPort.getAccessTokenExpirationMs(), userDto);
    }
}
