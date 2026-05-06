package com.nihongodev.platform.application.port.in;

import com.nihongodev.platform.application.dto.CorrectionSessionDto;

import java.util.List;
import java.util.UUID;

public interface GetCorrectionHistoryPort {
    List<CorrectionSessionDto> getUserHistory(UUID userId);
    CorrectionSessionDto getSessionById(UUID userId, UUID sessionId);
}
