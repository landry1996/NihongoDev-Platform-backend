package com.nihongodev.platform.application.port.in;

import com.nihongodev.platform.application.dto.UserBadgeDto;

import java.util.UUID;

public interface AwardBadgePort {
    UserBadgeDto execute(UUID userId, String badgeCode);
}
