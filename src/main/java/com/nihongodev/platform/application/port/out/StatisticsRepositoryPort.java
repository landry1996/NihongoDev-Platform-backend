package com.nihongodev.platform.application.port.out;

import com.nihongodev.platform.domain.model.UserStatistics;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StatisticsRepositoryPort {
    UserStatistics save(UserStatistics statistics);
    Optional<UserStatistics> findByUserId(UUID userId);
    List<UUID> findUserIdsWithActivitySince(LocalDateTime since);
}
