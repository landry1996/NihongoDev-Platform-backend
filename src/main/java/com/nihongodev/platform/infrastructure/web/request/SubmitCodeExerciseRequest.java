package com.nihongodev.platform.infrastructure.web.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SubmitCodeExerciseRequest(
    @NotBlank @Size(max = 5000) String response,
    int timeSpentSeconds
) {}
