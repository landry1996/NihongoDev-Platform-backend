package com.nihongodev.platform.application.port.in;

import com.nihongodev.platform.application.dto.PlatformAnalyticsDto;
import com.nihongodev.platform.application.dto.TopUserDto;
import java.util.List;

public interface GetPlatformAnalyticsPort {
    PlatformAnalyticsDto getOverview();
    List<TopUserDto> getTopUsers(int limit);
}
