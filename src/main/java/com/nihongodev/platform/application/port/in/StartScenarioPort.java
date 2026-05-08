package com.nihongodev.platform.application.port.in;

import com.nihongodev.platform.application.dto.CulturalScenarioDto;

import java.util.UUID;

public interface StartScenarioPort {
    CulturalScenarioDto startScenario(UUID userId, UUID scenarioId);
}
