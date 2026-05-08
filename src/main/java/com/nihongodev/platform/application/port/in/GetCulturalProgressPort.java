package com.nihongodev.platform.application.port.in;

import com.nihongodev.platform.application.dto.CulturalProgressDto;

import java.util.List;
import java.util.UUID;

public interface GetCulturalProgressPort {
    List<CulturalProgressDto> getProgress(UUID userId);
}
