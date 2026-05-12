package com.nihongodev.platform.application.port.in;

import com.nihongodev.platform.application.dto.UserBadgeDto;

import java.util.List;
import java.util.UUID;

public interface GetUserBadgesPort {
    List<UserBadgeDto> execute(UUID userId);
}
