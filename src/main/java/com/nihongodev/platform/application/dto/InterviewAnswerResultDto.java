package com.nihongodev.platform.application.dto;

import java.util.UUID;

public record InterviewAnswerResultDto(
        UUID answerId,
        UUID questionId,
        double overallScore,
        InterviewFeedbackDto feedback,
        boolean sessionComplete
) {}
