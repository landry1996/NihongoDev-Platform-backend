package com.nihongodev.platform.application.port.out;

import com.nihongodev.platform.domain.model.UserProgress;
import java.util.Optional;
import java.util.UUID;

public interface ProgressRepositoryPort {
    UserProgress save(UserProgress progress);
    Optional<UserProgress> findByUserId(UUID userId);
}
