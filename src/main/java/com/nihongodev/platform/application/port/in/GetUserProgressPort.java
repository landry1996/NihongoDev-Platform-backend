package com.nihongodev.platform.application.port.in;

import com.nihongodev.platform.application.dto.UserProgressDto;
import java.util.UUID;

public interface GetUserProgressPort {
    UserProgressDto execute(UUID userId);
}
