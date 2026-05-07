package com.nihongodev.platform.infrastructure.scheduling;

import com.nihongodev.platform.infrastructure.persistence.entity.UserProgressEntity;
import com.nihongodev.platform.infrastructure.persistence.repository.JpaUserProgressRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Component
public class StreakCalculationJob {

    private static final Logger log = LoggerFactory.getLogger(StreakCalculationJob.class);

    private final JpaUserProgressRepository progressRepository;

    public StreakCalculationJob(JpaUserProgressRepository progressRepository) {
        this.progressRepository = progressRepository;
    }

    @Scheduled(cron = "0 0 2 * * *")
    @Transactional
    public void resetBrokenStreaks() {
        log.info("Starting streak calculation job");
        LocalDate today = LocalDate.now();
        List<UserProgressEntity> allProgress = progressRepository.findAll();

        int resetCount = 0;
        for (UserProgressEntity progress : allProgress) {
            if (progress.getCurrentStreak() > 0 && progress.getLastActivityAt() != null) {
                LocalDate lastActive = progress.getLastActivityAt().toLocalDate();
                if (lastActive.isBefore(today.minusDays(1))) {
                    progress.setCurrentStreak(0);
                    progressRepository.save(progress);
                    resetCount++;
                }
            }
        }
        log.info("Streak calculation completed. Reset {} streaks", resetCount);
    }
}
