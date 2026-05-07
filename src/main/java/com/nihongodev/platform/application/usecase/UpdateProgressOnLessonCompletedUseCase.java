package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.port.in.UpdateProgressOnLessonCompletedPort;
import com.nihongodev.platform.application.port.out.EventPublisherPort;
import com.nihongodev.platform.application.port.out.LearningActivityRepositoryPort;
import com.nihongodev.platform.application.port.out.ModuleProgressRepositoryPort;
import com.nihongodev.platform.application.port.out.ProgressRepositoryPort;
import com.nihongodev.platform.domain.event.LessonCompletedEvent;
import com.nihongodev.platform.domain.event.ProgressUpdatedEvent;
import com.nihongodev.platform.domain.model.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateProgressOnLessonCompletedUseCase implements UpdateProgressOnLessonCompletedPort {

    private final ProgressRepositoryPort progressRepository;
    private final ModuleProgressRepositoryPort moduleProgressRepository;
    private final LearningActivityRepositoryPort activityRepository;
    private final EventPublisherPort eventPublisher;

    public UpdateProgressOnLessonCompletedUseCase(ProgressRepositoryPort progressRepository,
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
    public void execute(LessonCompletedEvent event) {
        if (activityRepository.existsByUserIdAndReferenceIdAndActivityType(
                event.userId(), event.lessonId(), ActivityType.LESSON_COMPLETED)) {
            return;
        }

        UserProgress progress = progressRepository.findByUserId(event.userId())
                .orElseGet(() -> UserProgress.initialize(event.userId()));

        progress.recordActivity(ActivityType.LESSON_COMPLETED, 0);
        progress.updateStreak(event.occurredAt());
        progress.setLastActivityAt(event.occurredAt());

        progressRepository.save(progress);

        ModuleProgress moduleProgress = moduleProgressRepository
                .findByUserIdAndModuleType(event.userId(), ModuleType.LESSON)
                .orElseGet(() -> ModuleProgress.initialize(event.userId(), ModuleType.LESSON));

        moduleProgress.recordCompletion(100.0);
        moduleProgressRepository.save(moduleProgress);

        int xpEarned = ActivityType.LESSON_COMPLETED.calculateXp(0);
        LearningActivity activity = LearningActivity.create(
                event.userId(), ActivityType.LESSON_COMPLETED,
                event.lessonId(), null, xpEarned);
        activity.addMetadata("lessonTitle", event.lessonTitle());
        activity.addMetadata("lessonType", event.lessonType());
        activity.addMetadata("lessonLevel", event.lessonLevel());
        activityRepository.save(activity);

        eventPublisher.publish("progress-events", ProgressUpdatedEvent.of(
                event.userId(), progress.getTotalXp(), progress.getLevel(),
                progress.getGlobalScore(), ActivityType.LESSON_COMPLETED));
    }
}
