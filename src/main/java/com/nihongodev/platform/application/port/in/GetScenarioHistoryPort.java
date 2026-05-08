package com.nihongodev.platform.application.port.in;

import com.nihongodev.platform.application.dto.ScenarioAttemptDto;

import java.util.List;
import java.util.UUID;

public interface GetScenarioHistoryPort {
    List<ScenarioAttemptDto> getUserHistory(UUID userId);
}
