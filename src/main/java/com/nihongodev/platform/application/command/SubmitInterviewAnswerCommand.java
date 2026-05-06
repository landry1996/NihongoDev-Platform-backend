package com.nihongodev.platform.application.command;

import java.util.UUID;

public record SubmitInterviewAnswerCommand(
        UUID sessionId,
        UUID questionId,
        String answerText,
        int timeSpentSeconds
) {}
