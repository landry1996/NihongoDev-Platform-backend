package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.port.in.UpdateProgressOnQuizCompletedPort;
import com.nihongodev.platform.application.port.out.EventPublisherPort;
import com.nihongodev.platform.application.port.out.LearningActivityRepositoryPort;
import com.nihongodev.platform.application.port.out.ModuleProgressRepositoryPort;
import com.nihongodev.platform.application.port.out.ProgressRepositoryPort;
import com.nihongodev.platform.domain.event.ProgressUpdatedEvent;
import com.nihongodev.platform.domain.event.QuizCompletedEvent;
import com.nihongodev.platform.domain.model.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateProgressOnQuizCompletedUseCase implements UpdateProgressOnQuizCompletedPort {

    private final ProgressRepositoryPort progressRepository;
    private final ModuleProgressRepositoryPort moduleProgressRepository;
    private final LearningActivityRepositoryPort activityRepository;
    private final EventPublisherPort eventPublisher;

    public UpdateProgressOnQuizCompletedUseCase(ProgressRepositoryPort progressRepository,
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
    public void execute(QuizCompletedEvent event) {
        if (activityRepository.existsByUserIdAndReferenceIdAndActivityType(
                event.userId(), event.attemptId(), ActivityType.QUIZ_COMPLETED)) {
            return;
        }

        UserProgress progress = progressRepository.findByUserId(event.userId())
                .orElseGet(() -> UserProgress.initialize(event.userId()));

        progress.recordActivity(ActivityType.QUIZ_COMPLETED, event.percentage());
        progress.updateGlobalScore(ActivityType.QUIZ_COMPLETED, event.percentage());
        progress.updateStreak(event.occurredAt());
        progress.setLastActivityAt(event.occurredAt());

        progressRepository.save(progress);

        ModuleProgress moduleProgress = moduleProgressRepository
                .findByUserIdAndModuleType(event.userId(), ModuleType.QUIZ)
                .orElseGet(() -> ModuleProgress.initialize(event.userId(), ModuleType.QUIZ));

        moduleProgress.recordCompletion(event.percentage());
        moduleProgressRepository.save(moduleProgress);

        int xpEarned = ActivityType.QUIZ_COMPLETED.calculateXp(event.percentage());
        LearningActivity activity = LearningActivity.create(
                event.userId(), ActivityType.QUIZ_COMPLETED,
                event.attemptId(), event.percentage(), xpEarned);
        activity.addMetadata("quizTitle", event.quizTitle());
        activity.addMetadata("passed", event.passed());
        activity.addMetadata("maxStreak", event.maxStreak());
        activity.addMetadata("mode", event.mode());
        activityRepository.save(activity);

        eventPublisher.publish("progress-events", ProgressUpdatedEvent.of(
                event.userId(), progress.getTotalXp(), progress.getLevel(),
                progress.getGlobalScore(), ActivityType.QUIZ_COMPLETED));
    }
}
