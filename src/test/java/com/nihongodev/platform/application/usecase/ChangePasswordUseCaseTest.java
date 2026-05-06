package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.command.ChangePasswordCommand;
import com.nihongodev.platform.application.port.out.PasswordEncoderPort;
import com.nihongodev.platform.application.port.out.UserRepositoryPort;
import com.nihongodev.platform.domain.exception.BusinessException;
import com.nihongodev.platform.domain.exception.ResourceNotFoundException;
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

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ChangePasswordUseCase")
class ChangePasswordUseCaseTest {

    @Mock private UserRepositoryPort userRepository;
    @Mock private PasswordEncoderPort passwordEncoder;

    private ChangePasswordUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new ChangePasswordUseCase(userRepository, passwordEncoder);
    }

    @Test
    @DisplayName("should change password successfully")
    void shouldChangePassword() {
        UUID userId = UUID.randomUUID();
        User user = User.create("Taro", "Yamada", "taro@example.com", "old_encoded", Role.LEARNER, JapaneseLevel.N5, null);
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("oldPassword", "old_encoded")).thenReturn(true);
        when(passwordEncoder.encode("newPassword")).thenReturn("new_encoded");
        when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        useCase.changePassword(new ChangePasswordCommand(userId, "oldPassword", "newPassword"));

        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("should throw when current password is wrong")
    void shouldThrowWhenCurrentPasswordWrong() {
        UUID userId = UUID.randomUUID();
        User user = User.create("Taro", "Yamada", "taro@example.com", "encoded", Role.LEARNER, JapaneseLevel.N5, null);
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "encoded")).thenReturn(false);

        assertThatThrownBy(() -> useCase.changePassword(new ChangePasswordCommand(userId, "wrong", "newPass")))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Current password is incorrect");
    }

    @Test
    @DisplayName("should throw when user not found")
    void shouldThrowWhenUserNotFound() {
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.changePassword(new ChangePasswordCommand(userId, "old", "new")))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
