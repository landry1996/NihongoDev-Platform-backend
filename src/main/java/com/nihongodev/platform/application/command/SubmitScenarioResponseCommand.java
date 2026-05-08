package com.nihongodev.platform.application.command;

import java.util.UUID;

public record SubmitScenarioResponseCommand(
        UUID scenarioId,
        String response,
        UUID selectedChoiceId,
        int timeSpentSeconds
) {}
