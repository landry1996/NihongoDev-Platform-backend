package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.dto.UserDto;
import com.nihongodev.platform.application.port.in.GetUserProfilePort;
import com.nihongodev.platform.application.port.out.UserRepositoryPort;
import com.nihongodev.platform.domain.exception.ResourceNotFoundException;
import com.nihongodev.platform.domain.model.User;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class GetUserProfileUseCase implements GetUserProfilePort {

    private final UserRepositoryPort userRepository;

    public GetUserProfileUseCase(UserRepositoryPort userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDto getProfile(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        return new UserDto(
                user.getId(), user.getFirstName(), user.getLastName(), user.getEmail(),
                user.getRole(), user.getJapaneseLevel(), user.getObjective(),
                user.getAvatarUrl(), user.isActive(), user.getCreatedAt()
        );
    }
}
