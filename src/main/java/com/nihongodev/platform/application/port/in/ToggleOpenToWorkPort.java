package com.nihongodev.platform.application.port.in;

import com.nihongodev.platform.application.dto.PublicProfileDto;

import java.util.UUID;

public interface ToggleOpenToWorkPort {
    PublicProfileDto execute(UUID userId);
}
