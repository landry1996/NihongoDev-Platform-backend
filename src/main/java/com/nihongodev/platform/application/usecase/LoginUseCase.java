package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.command.LoginCommand;
import com.nihongodev.platform.application.dto.AuthResponse;
import com.nihongodev.platform.application.dto.UserDto;
import com.nihongodev.platform.application.port.in.LoginPort;
import com.nihongodev.platform.application.port.out.*;
import com.nihongodev.platform.domain.exception.UnauthorizedException;
import com.nihongodev.platform.domain.model.RefreshToken;
import com.nihongodev.platform.domain.model.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LoginUseCase implements LoginPort {

    private final UserRepositoryPort userRepository;
    private final RefreshTokenRepositoryPort refreshTokenRepository;
    private final PasswordEncoderPort passwordEncoder;
    private final JwtPort jwtPort;

    public LoginUseCase(UserRepositoryPort userRepository,
                        RefreshTokenRepositoryPort refreshTokenRepository,
                        PasswordEncoderPort passwordEncoder,
                        JwtPort jwtPort) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtPort = jwtPort;
    }

    @Override
    @Transactional
    public AuthResponse login(LoginCommand command) {
        User user = userRepository.findByEmail(command.email())
                .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));

        if (!user.isActive()) {
            throw new UnauthorizedException("Account is deactivated");
        }

        if (!passwordEncoder.matches(command.password(), user.getPassword())) {
            throw new UnauthorizedException("Invalid credentials");
        }

        refreshTokenRepository.deleteByUserId(user.getId());

        String accessToken = jwtPort.generateAccessToken(user.getId(), user.getEmail(), user.getRole().name());
        String refreshTokenValue = jwtPort.generateRefreshToken();

        RefreshToken refreshToken = RefreshToken.create(user.getId(), refreshTokenValue, jwtPort.getRefreshTokenExpirationMs());
        refreshTokenRepository.save(refreshToken);

        UserDto userDto = new UserDto(
                user.getId(), user.getFirstName(), user.getLastName(), user.getEmail(),
                user.getRole(), user.getJapaneseLevel(), user.getObjective(),
                user.getAvatarUrl(), user.isActive(), user.getCreatedAt()
        );

        return AuthResponse.of(accessToken, refreshTokenValue, jwtPort.getAccessTokenExpirationMs(), userDto);
    }
}
