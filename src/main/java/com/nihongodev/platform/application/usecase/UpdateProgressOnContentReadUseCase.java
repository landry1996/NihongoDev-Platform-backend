package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.port.in.UpdateProgressOnContentReadPort;
import com.nihongodev.platform.application.port.out.LearningActivityRepositoryPort;
import com.nihongodev.platform.application.port.out.ModuleProgressRepositoryPort;
import com.nihongodev.platform.domain.event.ContentReadCompletedEvent;
import com.nihongodev.platform.domain.model.ActivityType;
import com.nihongodev.platform.domain.model.LearningActivity;
import com.nihongodev.platform.domain.model.ModuleProgress;
import com.nihongodev.platform.domain.model.ModuleType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateProgressOnContentReadUseCase implements UpdateProgressOnContentReadPort {

    private final LearningActivityRepositoryPort activityRepository;
    private final ModuleProgressRepositoryPort progressRepository;

    public UpdateProgressOnContentReadUseCase(LearningActivityRepositoryPort activityRepository,
                                              ModuleProgressRepositoryPort progressRepository) {
        this.activityRepository = activityRepository;
        this.progressRepository = progressRepository;
    }

    @Override
    @Transactional
    public void execute(ContentReadCompletedEvent event) {
        int xp = ActivityType.REAL_CONTENT_COMPLETED.calculateXp(event.comprehensionScore());
        LearningActivity activity = LearningActivity.create(
            event.userId(), ActivityType.REAL_CONTENT_COMPLETED,
            event.contentId(), event.comprehensionScore(), xp
        );
        activityRepository.save(activity);

        ModuleProgress progress = progressRepository
            .findByUserIdAndModuleType(event.userId(), ModuleType.REAL_CONTENT)
            .orElse(ModuleProgress.initialize(event.userId(), ModuleType.REAL_CONTENT));

        progress.recordCompletion(event.comprehensionScore());
        progressRepository.save(progress);
    }
}
