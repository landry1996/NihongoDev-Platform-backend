package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.command.ChangePasswordCommand;
import com.nihongodev.platform.application.port.in.ChangePasswordPort;
import com.nihongodev.platform.application.port.out.PasswordEncoderPort;
import com.nihongodev.platform.application.port.out.UserRepositoryPort;
import com.nihongodev.platform.domain.exception.BusinessException;
import com.nihongodev.platform.domain.exception.ResourceNotFoundException;
import com.nihongodev.platform.domain.model.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ChangePasswordUseCase implements ChangePasswordPort {

    private final UserRepositoryPort userRepository;
    private final PasswordEncoderPort passwordEncoder;

    public ChangePasswordUseCase(UserRepositoryPort userRepository, PasswordEncoderPort passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void changePassword(ChangePasswordCommand command) {
        User user = userRepository.findById(command.userId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", command.userId()));

        if (!passwordEncoder.matches(command.currentPassword(), user.getPassword())) {
            throw new BusinessException("Current password is incorrect");
        }

        String newEncodedPassword = passwordEncoder.encode(command.newPassword());
        user.changePassword(newEncodedPassword);
        userRepository.save(user);
    }
}
