package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.command.RegisterCommand;
import com.nihongodev.platform.application.dto.AuthResponse;
import com.nihongodev.platform.application.dto.UserDto;
import com.nihongodev.platform.application.port.in.RegisterUserPort;
import com.nihongodev.platform.application.port.out.*;
import com.nihongodev.platform.domain.event.UserRegisteredEvent;
import com.nihongodev.platform.domain.exception.BusinessException;
import com.nihongodev.platform.domain.model.RefreshToken;
import com.nihongodev.platform.domain.model.Role;
import com.nihongodev.platform.domain.model.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegisterUserUseCase implements RegisterUserPort {

    private final UserRepositoryPort userRepository;
    private final RefreshTokenRepositoryPort refreshTokenRepository;
    private final PasswordEncoderPort passwordEncoder;
    private final JwtPort jwtPort;
    private final EventPublisherPort eventPublisher;

    public RegisterUserUseCase(UserRepositoryPort userRepository,
                               RefreshTokenRepositoryPort refreshTokenRepository,
                               PasswordEncoderPort passwordEncoder,
                               JwtPort jwtPort,
                               EventPublisherPort eventPublisher) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtPort = jwtPort;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public AuthResponse register(RegisterCommand command) {
        if (userRepository.existsByEmail(command.email())) {
            throw new BusinessException("Email already registered: " + command.email());
        }

        String encodedPassword = passwordEncoder.encode(command.password());
        Role role = command.role() != null ? command.role() : Role.LEARNER;

        User user = User.create(
                command.firstName(),
                command.lastName(),
                command.email(),
                encodedPassword,
                role,
                command.japaneseLevel(),
                command.objective()
        );

        User savedUser = userRepository.save(user);

        String accessToken = jwtPort.generateAccessToken(savedUser.getId(), savedUser.getEmail(), savedUser.getRole().name());
        String refreshTokenValue = jwtPort.generateRefreshToken();

        RefreshToken refreshToken = RefreshToken.create(savedUser.getId(), refreshTokenValue, jwtPort.getRefreshTokenExpirationMs());
        refreshTokenRepository.save(refreshToken);

        eventPublisher.publish("user-events", UserRegisteredEvent.of(
                savedUser.getId(), savedUser.getEmail(), savedUser.getFirstName(), savedUser.getRole().name()
        ));

        UserDto userDto = mapToDto(savedUser);
        return AuthResponse.of(accessToken, refreshTokenValue, jwtPort.getAccessTokenExpirationMs(), userDto);
    }

    private UserDto mapToDto(User user) {
        return new UserDto(
                user.getId(), user.getFirstName(), user.getLastName(), user.getEmail(),
                user.getRole(), user.getJapaneseLevel(), user.getObjective(),
                user.getAvatarUrl(), user.isActive(), user.getCreatedAt()
        );
    }
}
