package com.nihongodev.platform.application.port.in;

import com.nihongodev.platform.application.command.GeneratePitchCommand;
import com.nihongodev.platform.application.dto.GeneratedPitchDto;

import java.util.UUID;

public interface GeneratePitchPort {
    GeneratedPitchDto generate(UUID userId, GeneratePitchCommand command);
}
