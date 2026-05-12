package com.nihongodev.platform.application.port.in;

import com.nihongodev.platform.application.command.CompleteReadingSessionCommand;
import com.nihongodev.platform.application.dto.ContentReadingSessionDto;

public interface CompleteReadingSessionPort {
    ContentReadingSessionDto execute(CompleteReadingSessionCommand command);
}
