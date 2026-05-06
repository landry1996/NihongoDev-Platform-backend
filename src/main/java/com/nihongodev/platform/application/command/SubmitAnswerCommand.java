package com.nihongodev.platform.application.command;

import java.util.UUID;

public record SubmitAnswerCommand(
        UUID attemptId,
        UUID questionId,
        String userAnswer,
        int timeSpentSeconds
) {}
