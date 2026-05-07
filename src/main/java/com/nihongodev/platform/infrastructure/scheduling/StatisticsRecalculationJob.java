package com.nihongodev.platform.infrastructure.scheduling;

import com.nihongodev.platform.application.port.in.RecalculateStatisticsPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class StatisticsRecalculationJob {

    private static final Logger log = LoggerFactory.getLogger(StatisticsRecalculationJob.class);

    private final RecalculateStatisticsPort recalculateStatisticsPort;

    public StatisticsRecalculationJob(RecalculateStatisticsPort recalculateStatisticsPort) {
        this.recalculateStatisticsPort = recalculateStatisticsPort;
    }

    @Scheduled(fixedRate = 15, timeUnit = TimeUnit.MINUTES)
    public void recalculate() {
        log.info("Starting statistics recalculation job");
        try {
            recalculateStatisticsPort.recalculateAll();
            log.info("Statistics recalculation completed");
        } catch (Exception e) {
            log.error("Error during statistics recalculation: {}", e.getMessage(), e);
        }
    }
}
