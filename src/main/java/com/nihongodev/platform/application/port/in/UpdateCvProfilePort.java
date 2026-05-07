package com.nihongodev.platform.application.port.in;

import com.nihongodev.platform.application.command.UpdateCvProfileCommand;
import com.nihongodev.platform.application.dto.CvProfileDto;

import java.util.UUID;

public interface UpdateCvProfilePort {
    CvProfileDto update(UUID userId, UpdateCvProfileCommand command);
}
