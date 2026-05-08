package com.nihongodev.platform.application.port.in;

import com.nihongodev.platform.application.command.SubmitScenarioResponseCommand;
import com.nihongodev.platform.application.dto.ScenarioAttemptDto;

import java.util.UUID;

public interface SubmitScenarioResponsePort {
    ScenarioAttemptDto submit(UUID userId, SubmitScenarioResponseCommand command);
}
