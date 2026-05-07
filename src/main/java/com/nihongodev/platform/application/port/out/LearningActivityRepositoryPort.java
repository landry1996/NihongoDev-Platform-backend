package com.nihongodev.platform.application.port.out;

import com.nihongodev.platform.domain.model.ActivityType;
import com.nihongodev.platform.domain.model.LearningActivity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface LearningActivityRepositoryPort {
    LearningActivity save(LearningActivity activity);
    Page<LearningActivity> findByUserId(UUID userId, Pageable pageable);
    List<LearningActivity> findByUserIdAndOccurredAfter(UUID userId, LocalDateTime after);
    boolean existsByUserIdAndReferenceIdAndActivityType(UUID userId, UUID referenceId, ActivityType activityType);
    long countDistinctDaysActiveAfter(UUID userId, LocalDateTime after);
}
