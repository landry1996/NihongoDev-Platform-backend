package com.nihongodev.platform.application.port.in;

import com.nihongodev.platform.application.dto.UserStatisticsDto;
import java.util.UUID;

public interface GetUserStatisticsPort {
    UserStatisticsDto execute(UUID userId);
}
