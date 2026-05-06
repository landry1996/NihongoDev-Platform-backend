package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.command.RegisterCommand;
import com.nihongodev.platform.application.dto.AuthResponse;
import com.nihongodev.platform.application.port.out.*;
import com.nihongodev.platform.domain.exception.BusinessException;
import com.nihongodev.platform.domain.model.JapaneseLevel;
import com.nihongodev.platform.domain.model.Role;
import com.nihongodev.platform.domain.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RegisterUserUseCase")
class RegisterUserUseCaseTest {

    @Mock private UserRepositoryPort userRepository;
    @Mock private RefreshTokenRepositoryPort refreshTokenRepository;
    @Mock private PasswordEncoderPort passwordEncoder;
    @Mock private JwtPort jwtPort;
    @Mock private EventPublisherPort eventPublisher;

    private RegisterUserUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new RegisterUserUseCase(userRepository, refreshTokenRepository, passwordEncoder, jwtPort, eventPublisher);
    }

    @Test
    @DisplayName("should register a new user successfully")
    void shouldRegisterNewUser() {
        RegisterCommand command = new RegisterCommand("Taro", "Yamada", "taro@example.com", "password123", Role.LEARNER, JapaneseLevel.BEGINNER, "Work in Japan");

        when(userRepository.existsByEmail("taro@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encoded_password");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(UUID.randomUUID());
            return u;
        });
        when(jwtPort.generateAccessToken(any(), anyString(), anyString())).thenReturn("access_token");
        when(jwtPort.generateRefreshToken()).thenReturn("refresh_token");
        when(jwtPort.getAccessTokenExpirationMs()).thenReturn(3600000L);
        when(jwtPort.getRefreshTokenExpirationMs()).thenReturn(604800000L);
        when(refreshTokenRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        AuthResponse response = useCase.register(command);

        assertThat(response).isNotNull();
        assertThat(response.accessToken()).isEqualTo("access_token");
        assertThat(response.refreshToken()).isEqualTo("refresh_token");
        assertThat(response.tokenType()).isEqualTo("Bearer");
        assertThat(response.user().firstName()).isEqualTo("Taro");
        assertThat(response.user().email()).isEqualTo("taro@example.com");
        assertThat(response.user().role()).isEqualTo(Role.LEARNER);

        verify(userRepository).save(any(User.class));
        verify(eventPublisher).publish(eq("user-events"), any());
    }

    @Test
    @DisplayName("should throw exception when email already exists")
    void shouldThrowWhenEmailExists() {
        RegisterCommand command = new RegisterCommand("Taro", "Yamada", "existing@example.com", "password123", null, null, null);

        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        assertThatThrownBy(() -> useCase.register(command))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Email already registered");

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("should default to LEARNER role when role is null")
    void shouldDefaultToLearnerRole() {
        RegisterCommand command = new RegisterCommand("Taro", "Yamada", "taro@example.com", "password123", null, null, null);

        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(UUID.randomUUID());
            return u;
        });
        when(jwtPort.generateAccessToken(any(), anyString(), anyString())).thenReturn("token");
        when(jwtPort.generateRefreshToken()).thenReturn("refresh");
        when(jwtPort.getAccessTokenExpirationMs()).thenReturn(3600000L);
        when(jwtPort.getRefreshTokenExpirationMs()).thenReturn(604800000L);
        when(refreshTokenRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        AuthResponse response = useCase.register(command);

        assertThat(response.user().role()).isEqualTo(Role.LEARNER);
    }
}
