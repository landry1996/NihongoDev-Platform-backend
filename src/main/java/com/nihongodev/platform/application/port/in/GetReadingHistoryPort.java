package com.nihongodev.platform.application.port.in;

import com.nihongodev.platform.application.dto.ContentReadingSessionDto;

import java.util.List;
import java.util.UUID;

public interface GetReadingHistoryPort {
    List<ContentReadingSessionDto> execute(UUID userId);
}
