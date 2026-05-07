package com.nihongodev.platform.application.port.in;

import com.nihongodev.platform.domain.event.TextCorrectedEvent;

public interface UpdateProgressOnCorrectionCompletedPort {
    void execute(TextCorrectedEvent event);
}
