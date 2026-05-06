package com.nihongodev.platform.application.port.in;

import com.nihongodev.platform.application.command.CorrectTextCommand;
import com.nihongodev.platform.application.dto.CorrectionSessionDto;

import java.util.UUID;

public interface CorrectTextPort {
    CorrectionSessionDto correct(UUID userId, CorrectTextCommand command);
}
