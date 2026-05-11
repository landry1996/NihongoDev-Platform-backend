package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.port.in.UpdateProgressOnScenarioCompletedPort;
import com.nihongodev.platform.application.port.out.EventPublisherPort;
import com.nihongodev.platform.application.port.out.LearningActivityRepositoryPort;
import com.nihongodev.platform.application.port.out.ModuleProgressRepositoryPort;
import com.nihongodev.platform.application.port.out.ProgressRepositoryPort;
import com.nihongodev.platform.domain.event.ProgressUpdatedEvent;
import com.nihongodev.platform.domain.event.ScenarioCompletedEvent;
import com.nihongodev.platform.domain.model.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateProgressOnScenarioCompletedUseCase implements UpdateProgressOnScenarioCompletedPort {

    private final ProgressRepositoryPort progressRepository;
    private final ModuleProgressRepositoryPort moduleProgressRepository;
    private final LearningActivityRepositoryPort activityRepository;
    private final EventPublisherPort eventPublisher;

    public UpdateProgressOnScenarioCompletedUseCase(ProgressRepositoryPort progressRepository,
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
    public void execute(ScenarioCompletedEvent event) {
        if (activityRepository.existsByUserIdAndReferenceIdAndActivityType(
                event.userId(), event.scenarioId(), ActivityType.CULTURAL_SCENARIO_COMPLETED)) {
            return;
        }

        UserProgress progress = progressRepository.findByUserId(event.userId())
                .orElseGet(() -> UserProgress.initialize(event.userId()));

        progress.recordActivity(ActivityType.CULTURAL_SCENARIO_COMPLETED, event.overallScore());
        progress.updateStreak(event.occurredAt());
        progress.setLastActivityAt(event.occurredAt());

        progressRepository.save(progress);

        ModuleProgress moduleProgress = moduleProgressRepository
                .findByUserIdAndModuleType(event.userId(), ModuleType.CULTURAL)
                .orElseGet(() -> ModuleProgress.initialize(event.userId(), ModuleType.CULTURAL));

        moduleProgress.recordCompletion(event.overallScore());
        moduleProgressRepository.save(moduleProgress);

        int xpEarned = ActivityType.CULTURAL_SCENARIO_COMPLETED.calculateXp(event.overallScore());
        LearningActivity activity = LearningActivity.create(
                event.userId(), ActivityType.CULTURAL_SCENARIO_COMPLETED,
                event.scenarioId(), null, xpEarned);
        activity.addMetadata("category", event.category().name());
        activity.addMetadata("overallScore", String.valueOf(event.overallScore()));
        activityRepository.save(activity);

        eventPublisher.publish("progress-events", ProgressUpdatedEvent.of(
                event.userId(), progress.getTotalXp(), progress.getLevel(),
                progress.getGlobalScore(), ActivityType.CULTURAL_SCENARIO_COMPLETED));
    }
}
