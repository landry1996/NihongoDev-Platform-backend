package com.nihongodev.platform.infrastructure.web.request;

import jakarta.validation.constraints.Size;

import java.util.UUID;

public record SubmitScenarioResponseRequest(
        @Size(max = 2000) String response,
        UUID selectedChoiceId,
        int timeSpentSeconds
) {}
