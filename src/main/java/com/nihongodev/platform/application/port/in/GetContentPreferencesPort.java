package com.nihongodev.platform.application.port.in;

import com.nihongodev.platform.application.dto.UserContentPreferenceDto;

import java.util.UUID;

public interface GetContentPreferencesPort {
    UserContentPreferenceDto execute(UUID userId);
}
