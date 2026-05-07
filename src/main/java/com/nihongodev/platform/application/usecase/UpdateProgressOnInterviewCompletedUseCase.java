package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.port.in.UpdateProgressOnInterviewCompletedPort;
import com.nihongodev.platform.application.port.out.EventPublisherPort;
import com.nihongodev.platform.application.port.out.LearningActivityRepositoryPort;
import com.nihongodev.platform.application.port.out.ModuleProgressRepositoryPort;
import com.nihongodev.platform.application.port.out.ProgressRepositoryPort;
import com.nihongodev.platform.domain.event.InterviewCompletedEvent;
import com.nihongodev.platform.domain.event.ProgressUpdatedEvent;
import com.nihongodev.platform.domain.model.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class UpdateProgressOnInterviewCompletedUseCase implements UpdateProgressOnInterviewCompletedPort {

    private final ProgressRepositoryPort progressRepository;
    private final ModuleProgressRepositoryPort moduleProgressRepository;
    private final LearningActivityRepositoryPort activityRepository;
    private final EventPublisherPort eventPublisher;

    public UpdateProgressOnInterviewCompletedUseCase(ProgressRepositoryPort progressRepository,
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
    public void execute(InterviewCompletedEvent event) {
        if (activityRepository.existsByUserIdAndReferenceIdAndActivityType(
                event.userId(), event.sessionId(), ActivityType.INTERVIEW_COMPLETED)) {
            return;
        }

        UserProgress progress = progressRepository.findByUserId(event.userId())
                .orElseGet(() -> UserProgress.initialize(event.userId()));

        progress.recordActivity(ActivityType.INTERVIEW_COMPLETED, event.overallScore());
        progress.updateGlobalScore(ActivityType.INTERVIEW_COMPLETED, event.overallScore());
        LocalDateTime now = LocalDateTime.now();
        progress.updateStreak(now);
        progress.setLastActivityAt(now);

        progressRepository.save(progress);

        ModuleProgress moduleProgress = moduleProgressRepository
                .findByUserIdAndModuleType(event.userId(), ModuleType.INTERVIEW)
                .orElseGet(() -> ModuleProgress.initialize(event.userId(), ModuleType.INTERVIEW));

        moduleProgress.recordCompletion(event.overallScore());
        moduleProgressRepository.save(moduleProgress);

        int xpEarned = ActivityType.INTERVIEW_COMPLETED.calculateXp(event.overallScore());
        LearningActivity activity = LearningActivity.create(
                event.userId(), ActivityType.INTERVIEW_COMPLETED,
                event.sessionId(), event.overallScore(), xpEarned);
        activity.addMetadata("interviewType", event.interviewType());
        activity.addMetadata("passed", event.passed());
        activity.addMetadata("durationSeconds", event.durationSeconds());
        activityRepository.save(activity);

        eventPublisher.publish("progress-events", ProgressUpdatedEvent.of(
                event.userId(), progress.getTotalXp(), progress.getLevel(),
                progress.getGlobalScore(), ActivityType.INTERVIEW_COMPLETED));
    }
}
