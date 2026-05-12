package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.dto.UserContentPreferenceDto;
import com.nihongodev.platform.application.port.in.GetContentPreferencesPort;
import com.nihongodev.platform.application.port.out.UserContentPreferenceRepositoryPort;
import com.nihongodev.platform.domain.model.JapaneseLevel;
import com.nihongodev.platform.domain.model.UserContentPreference;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class GetContentPreferencesUseCase implements GetContentPreferencesPort {

    private final UserContentPreferenceRepositoryPort preferenceRepository;

    public GetContentPreferencesUseCase(UserContentPreferenceRepositoryPort preferenceRepository) {
        this.preferenceRepository = preferenceRepository;
    }

    @Override
    public UserContentPreferenceDto execute(UUID userId) {
        UserContentPreference preference = preferenceRepository.findByUserId(userId)
            .orElse(UserContentPreference.defaults(userId, JapaneseLevel.N3));

        return new UserContentPreferenceDto(
            preference.userId(),
            preference.preferredDomains(),
            preference.currentLevel(),
            preference.maxDifficulty(),
            preference.preferredReadingMinutes(),
            preference.preferredSources()
        );
    }
}
