package com.nihongodev.platform.application.port.out;

import com.nihongodev.platform.application.dto.TopUserDto;
import com.nihongodev.platform.domain.model.ModuleType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface AnalyticsQueryPort {
    long countActiveUsers(LocalDateTime since);
    double averageGlobalScore();
    Map<ModuleType, Double> averageScoreByModule();
    long countTotalActivities();
    List<TopUserDto> topUsers(int limit);
}
