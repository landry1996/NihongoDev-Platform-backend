package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.command.LoginCommand;
import com.nihongodev.platform.application.dto.AuthResponse;
import com.nihongodev.platform.application.port.out.JwtPort;
import com.nihongodev.platform.application.port.out.PasswordEncoderPort;
import com.nihongodev.platform.application.port.out.RefreshTokenRepositoryPort;
import com.nihongodev.platform.application.port.out.UserRepositoryPort;
import com.nihongodev.platform.domain.exception.UnauthorizedException;
import com.nihongodev.platform.domain.model.JapaneseLevel;
import com.nihongodev.platform.domain.model.Role;
import com.nihongodev.platform.domain.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("LoginUseCase")
class LoginUseCaseTest {

    @Mock private UserRepositoryPort userRepository;
    @Mock private RefreshTokenRepositoryPort refreshTokenRepository;
    @Mock private PasswordEncoderPort passwordEncoder;
    @Mock private JwtPort jwtPort;

    private LoginUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new LoginUseCase(userRepository, refreshTokenRepository, passwordEncoder, jwtPort);
    }

    @Test
    @DisplayName("should login successfully with valid credentials")
    void shouldLoginSuccessfully() {
        User user = User.create("Taro", "Yamada", "taro@example.com", "encoded", Role.LEARNER, JapaneseLevel.N5, "Japan");
        user.setId(UUID.randomUUID());

        when(userRepository.findByEmail("taro@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "encoded")).thenReturn(true);
        when(jwtPort.generateAccessToken(any(), anyString(), anyString())).thenReturn("access_token");
        when(jwtPort.generateRefreshToken()).thenReturn("refresh_token");
        when(jwtPort.getAccessTokenExpirationMs()).thenReturn(3600000L);
        when(jwtPort.getRefreshTokenExpirationMs()).thenReturn(604800000L);
        when(refreshTokenRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        AuthResponse response = useCase.login(new LoginCommand("taro@example.com", "password123"));

        assertThat(response.accessToken()).isEqualTo("access_token");
        assertThat(response.user().email()).isEqualTo("taro@example.com");
        verify(refreshTokenRepository).deleteByUserId(user.getId());
    }

    @Test
    @DisplayName("should throw when user not found")
    void shouldThrowWhenUserNotFound() {
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.login(new LoginCommand("unknown@example.com", "password")))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("Invalid credentials");
    }

    @Test
    @DisplayName("should throw when password is wrong")
    void shouldThrowWhenPasswordWrong() {
        User user = User.create("Taro", "Yamada", "taro@example.com", "encoded", Role.LEARNER, JapaneseLevel.N5, null);
        user.setId(UUID.randomUUID());

        when(userRepository.findByEmail("taro@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "encoded")).thenReturn(false);

        assertThatThrownBy(() -> useCase.login(new LoginCommand("taro@example.com", "wrong")))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("Invalid credentials");
    }

    @Test
    @DisplayName("should throw when account is deactivated")
    void shouldThrowWhenDeactivated() {
        User user = User.create("Taro", "Yamada", "taro@example.com", "encoded", Role.LEARNER, JapaneseLevel.N5, null);
        user.setId(UUID.randomUUID());
        user.deactivate();

        when(userRepository.findByEmail("taro@example.com")).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> useCase.login(new LoginCommand("taro@example.com", "password")))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("Account is deactivated");
    }
}
