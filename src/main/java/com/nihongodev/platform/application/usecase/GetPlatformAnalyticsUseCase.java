package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.dto.PlatformAnalyticsDto;
import com.nihongodev.platform.application.dto.TopUserDto;
import com.nihongodev.platform.application.port.in.GetPlatformAnalyticsPort;
import com.nihongodev.platform.application.port.out.AnalyticsQueryPort;
import com.nihongodev.platform.domain.model.ModuleType;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class GetPlatformAnalyticsUseCase implements GetPlatformAnalyticsPort {

    private final AnalyticsQueryPort analyticsQueryPort;

    public GetPlatformAnalyticsUseCase(AnalyticsQueryPort analyticsQueryPort) {
        this.analyticsQueryPort = analyticsQueryPort;
    }

    @Override
    public PlatformAnalyticsDto getOverview() {
        long activeUsers7d = analyticsQueryPort.countActiveUsers(LocalDateTime.now().minusDays(7));
        long activeUsers30d = analyticsQueryPort.countActiveUsers(LocalDateTime.now().minusDays(30));
        double avgScore = analyticsQueryPort.averageGlobalScore();
        long totalActivities = analyticsQueryPort.countTotalActivities();
        Map<ModuleType, Double> scoreByModule = analyticsQueryPort.averageScoreByModule();

        long totalUsers = activeUsers30d;

        return new PlatformAnalyticsDto(
                totalUsers, activeUsers7d, activeUsers30d,
                avgScore, totalActivities, scoreByModule
        );
    }

    @Override
    public List<TopUserDto> getTopUsers(int limit) {
        return analyticsQueryPort.topUsers(limit);
    }
}
