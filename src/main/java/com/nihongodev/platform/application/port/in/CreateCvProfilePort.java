package com.nihongodev.platform.application.port.in;

import com.nihongodev.platform.application.command.CreateCvProfileCommand;
import com.nihongodev.platform.application.dto.CvProfileDto;

import java.util.UUID;

public interface CreateCvProfilePort {
    CvProfileDto create(UUID userId, CreateCvProfileCommand command);
}
