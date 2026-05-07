package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.port.in.UpdateProgressOnCorrectionCompletedPort;
import com.nihongodev.platform.application.port.out.EventPublisherPort;
import com.nihongodev.platform.application.port.out.LearningActivityRepositoryPort;
import com.nihongodev.platform.application.port.out.ModuleProgressRepositoryPort;
import com.nihongodev.platform.application.port.out.ProgressRepositoryPort;
import com.nihongodev.platform.domain.event.ProgressUpdatedEvent;
import com.nihongodev.platform.domain.event.TextCorrectedEvent;
import com.nihongodev.platform.domain.model.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateProgressOnCorrectionCompletedUseCase implements UpdateProgressOnCorrectionCompletedPort {

    private final ProgressRepositoryPort progressRepository;
    private final ModuleProgressRepositoryPort moduleProgressRepository;
    private final LearningActivityRepositoryPort activityRepository;
    private final EventPublisherPort eventPublisher;

    public UpdateProgressOnCorrectionCompletedUseCase(ProgressRepositoryPort progressRepository,
                                                      ModuleProgressRepositoryPort moduleProgressRepository,
                                                      LearningActivityRepositoryPort activityRepository,
                                                      EventPublisherPort eventPublisher) {
        this.progressRepository = progressRepository;
        this.moduleProgressRepository = moduleProgressRepository;
        this.activityRepository = activityRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public void execute(TextCorrectedEvent event) {
        if (activityRepository.existsByUserIdAndReferenceIdAndActivityType(
                event.userId(), event.sessionId(), ActivityType.CORRECTION_COMPLETED)) {
            return;
        }

        UserProgress progress = progressRepository.findByUserId(event.userId())
                .orElseGet(() -> UserProgress.initialize(event.userId()));

        progress.recordActivity(ActivityType.CORRECTION_COMPLETED, event.overallScore());
        progress.updateGlobalScore(ActivityType.CORRECTION_COMPLETED, event.overallScore());
        progress.updateStreak(event.correctedAt());
        progress.setLastActivityAt(event.correctedAt());

        progressRepository.save(progress);

        ModuleProgress moduleProgress = moduleProgressRepository
                .findByUserIdAndModuleType(event.userId(), ModuleType.CORRECTION)
                .orElseGet(() -> ModuleProgress.initialize(event.userId(), ModuleType.CORRECTION));

        moduleProgress.recordCompletion(event.overallScore());
        moduleProgressRepository.save(moduleProgress);

        int xpEarned = ActivityType.CORRECTION_COMPLETED.calculateXp(event.overallScore());
        LearningActivity activity = LearningActivity.create(
                event.userId(), ActivityType.CORRECTION_COMPLETED,
                event.sessionId(), event.overallScore(), xpEarned);
        activity.addMetadata("textType", event.textType());
        activity.addMetadata("totalAnnotations", event.totalAnnotations());
        activity.addMetadata("errorCount", event.errorCount());
        activityRepository.save(activity);

        eventPublisher.publish("progress-events", ProgressUpdatedEvent.of(
                event.userId(), progress.getTotalXp(), progress.getLevel(),
                progress.getGlobalScore(), ActivityType.CORRECTION_COMPLETED));
    }
}
