package com.nihongodev.platform.application.command;

import java.util.UUID;

public record SubmitCodeExerciseResponseCommand(
    UUID userId,
    UUID exerciseId,
    String response,
    int timeSpentSeconds
) {}
