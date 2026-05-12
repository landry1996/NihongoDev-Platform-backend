package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.command.UpdateContentPreferencesCommand;
import com.nihongodev.platform.application.dto.UserContentPreferenceDto;
import com.nihongodev.platform.application.port.in.UpdateContentPreferencesPort;
import com.nihongodev.platform.application.port.out.UserContentPreferenceRepositoryPort;
import com.nihongodev.platform.domain.model.UserContentPreference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateContentPreferencesUseCase implements UpdateContentPreferencesPort {

    private final UserContentPreferenceRepositoryPort preferenceRepository;

    public UpdateContentPreferencesUseCase(UserContentPreferenceRepositoryPort preferenceRepository) {
        this.preferenceRepository = preferenceRepository;
    }

    @Override
    @Transactional
    public UserContentPreferenceDto execute(UpdateContentPreferencesCommand command) {
        UserContentPreference preference = new UserContentPreference(
            command.userId(),
            command.preferredDomains(),
            command.currentLevel(),
            command.maxDifficulty(),
            command.preferredReadingMinutes(),
            command.preferredSources()
        );

        preference = preferenceRepository.save(preference);

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
