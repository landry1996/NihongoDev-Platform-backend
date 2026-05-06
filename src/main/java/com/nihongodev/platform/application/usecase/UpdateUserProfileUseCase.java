package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.command.UpdateProfileCommand;
import com.nihongodev.platform.application.dto.UserDto;
import com.nihongodev.platform.application.port.in.UpdateUserProfilePort;
import com.nihongodev.platform.application.port.out.UserRepositoryPort;
import com.nihongodev.platform.domain.exception.ResourceNotFoundException;
import com.nihongodev.platform.domain.model.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateUserProfileUseCase implements UpdateUserProfilePort {

    private final UserRepositoryPort userRepository;

    public UpdateUserProfileUseCase(UserRepositoryPort userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public UserDto updateProfile(UpdateProfileCommand command) {
        User user = userRepository.findById(command.userId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", command.userId()));

        user.updateProfile(command.firstName(), command.lastName(), command.japaneseLevel(), command.objective());
        User saved = userRepository.save(user);

        return new UserDto(
                saved.getId(), saved.getFirstName(), saved.getLastName(), saved.getEmail(),
                saved.getRole(), saved.getJapaneseLevel(), saved.getObjective(),
                saved.getAvatarUrl(), saved.isActive(), saved.getCreatedAt()
        );
    }
}
