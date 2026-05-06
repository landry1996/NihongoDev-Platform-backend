package com.nihongodev.platform.application.port.in;

import com.nihongodev.platform.application.dto.UserDto;
import java.util.UUID;

public interface GetUserProfilePort {
    UserDto getProfile(UUID userId);
}
