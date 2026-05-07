package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.dto.UserProgressDto;
import com.nihongodev.platform.application.port.in.GetUserProgressPort;
import com.nihongodev.platform.application.port.out.ProgressRepositoryPort;
import com.nihongodev.platform.domain.model.UserProgress;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class GetUserProgressUseCase implements GetUserProgressPort {

    private final ProgressRepositoryPort progressRepository;

    public GetUserProgressUseCase(ProgressRepositoryPort progressRepository) {
        this.progressRepository = progressRepository;
    }

    @Override
    public UserProgressDto execute(UUID userId) {
        UserProgress progress = progressRepository.findByUserId(userId)
                .orElseGet(() -> UserProgress.initialize(userId));

        return new UserProgressDto(
                progress.getUserId(),
                progress.getTotalLessonsCompleted(),
                progress.getTotalQuizzesCompleted(),
                progress.getTotalInterviewsCompleted(),
                progress.getTotalCorrectionsCompleted(),
                progress.getGlobalScore(),
                progress.getCurrentStreak(),
                progress.getLongestStreak(),
                progress.getLevel(),
                progress.getTotalXp(),
                progress.getLastActivityAt()
        );
    }
}
