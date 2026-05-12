package com.nihongodev.platform.application.port.out;

import com.nihongodev.platform.domain.model.UserContentPreference;

import java.util.Optional;
import java.util.UUID;

public interface UserContentPreferenceRepositoryPort {
    UserContentPreference save(UserContentPreference preference);
    Optional<UserContentPreference> findByUserId(UUID userId);
}
