package com.nihongodev.platform.application.port.in;

import com.nihongodev.platform.application.command.IngestContentCommand;
import com.nihongodev.platform.application.dto.RealContentDto;

public interface IngestContentPort {
    RealContentDto execute(IngestContentCommand command);
}
