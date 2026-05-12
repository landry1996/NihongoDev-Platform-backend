package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.port.in.UpdateProgressOnCodeExerciseCompletedPort;
import com.nihongodev.platform.application.port.out.EventPublisherPort;
import com.nihongodev.platform.application.port.out.LearningActivityRepositoryPort;
import com.nihongodev.platform.application.port.out.ModuleProgressRepositoryPort;
import com.nihongodev.platform.application.port.out.ProgressRepositoryPort;
import com.nihongodev.platform.domain.event.CodeExerciseCompletedEvent;
import com.nihongodev.platform.domain.event.ProgressUpdatedEvent;
import com.nihongodev.platform.domain.model.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateProgressOnCodeExerciseCompletedUseCase implements UpdateProgressOnCodeExerciseCompletedPort {

    private final ProgressRepositoryPort progressRepository;
    private final ModuleProgressRepositoryPort moduleProgressRepository;
    private final LearningActivityRepositoryPort activityRepository;
    private final EventPublisherPort eventPublisher;

    public UpdateProgressOnCodeExerciseCompletedUseCase(ProgressRepositoryPort progressRepository,
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
    public void execute(CodeExerciseCompletedEvent event) {
        if (activityRepository.existsByUserIdAndReferenceIdAndActivityType(
                event.userId(), event.exerciseId(), ActivityType.CODE_EXERCISE_COMPLETED)) {
            return;
        }

        UserProgress progress = progressRepository.findByUserId(event.userId())
                .orElseGet(() -> UserProgress.initialize(event.userId()));

        progress.recordActivity(ActivityType.CODE_EXERCISE_COMPLETED, event.overallScore());
        progress.updateGlobalScore(ActivityType.CODE_EXERCISE_COMPLETED, event.overallScore());
        progress.updateStreak(event.occurredAt());
        progress.setLastActivityAt(event.occurredAt());

        progressRepository.save(progress);

        ModuleProgress moduleProgress = moduleProgressRepository
                .findByUserIdAndModuleType(event.userId(), ModuleType.CODE_REVIEW)
                .orElseGet(() -> ModuleProgress.initialize(event.userId(), ModuleType.CODE_REVIEW));

        moduleProgress.recordCompletion(event.overallScore());
        moduleProgressRepository.save(moduleProgress);

        int xpEarned = ActivityType.CODE_EXERCISE_COMPLETED.calculateXp(event.overallScore());
        LearningActivity activity = LearningActivity.create(
                event.userId(), ActivityType.CODE_EXERCISE_COMPLETED,
                event.exerciseId(), (double) event.overallScore(), xpEarned);
        activity.addMetadata("exerciseType", event.exerciseType().name());
        activityRepository.save(activity);

        eventPublisher.publish("progress-events", ProgressUpdatedEvent.of(
                event.userId(), progress.getTotalXp(), progress.getLevel(),
                progress.getGlobalScore(), ActivityType.CODE_EXERCISE_COMPLETED));
    }
}
