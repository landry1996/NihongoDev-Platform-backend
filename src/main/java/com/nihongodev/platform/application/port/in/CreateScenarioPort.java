package com.nihongodev.platform.application.port.in;

import com.nihongodev.platform.application.command.CreateScenarioCommand;
import com.nihongodev.platform.application.dto.CulturalScenarioDto;

public interface CreateScenarioPort {
    CulturalScenarioDto create(CreateScenarioCommand command);
}
