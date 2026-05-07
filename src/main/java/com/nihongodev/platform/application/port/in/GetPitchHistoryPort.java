package com.nihongodev.platform.application.port.in;

import com.nihongodev.platform.application.dto.GeneratedPitchDto;
import com.nihongodev.platform.domain.model.PitchType;

import java.util.List;
import java.util.UUID;

public interface GetPitchHistoryPort {
    List<GeneratedPitchDto> getHistory(UUID userId, PitchType type);
    GeneratedPitchDto getLatest(UUID userId, PitchType type);
}
