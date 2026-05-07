package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.dto.LearningActivityDto;
import com.nihongodev.platform.application.port.in.GetUserActivityHistoryPort;
import com.nihongodev.platform.application.port.out.LearningActivityRepositoryPort;
import com.nihongodev.platform.domain.model.LearningActivity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class GetUserActivityHistoryUseCase implements GetUserActivityHistoryPort {

    private final LearningActivityRepositoryPort activityRepository;

    public GetUserActivityHistoryUseCase(LearningActivityRepositoryPort activityRepository) {
        this.activityRepository = activityRepository;
    }

    @Override
    public Page<LearningActivityDto> execute(UUID userId, Pageable pageable) {
        return activityRepository.findByUserId(userId, pageable)
                .map(this::toDto);
    }

    private LearningActivityDto toDto(LearningActivity a) {
        return new LearningActivityDto(
                a.getActivityType(),
                a.getReferenceId(),
                a.getScore(),
                a.getXpEarned(),
                a.getMetadata(),
                a.getOccurredAt()
        );
    }
}
