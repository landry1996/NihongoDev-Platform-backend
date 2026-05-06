package com.nihongodev.platform.application.dto;

import com.nihongodev.platform.domain.model.InterviewDifficulty;
import com.nihongodev.platform.domain.model.InterviewPhase;
import com.nihongodev.platform.domain.model.InterviewType;

import java.util.UUID;

public record InterviewQuestionDto(
        UUID id,
        InterviewType interviewType,
        InterviewDifficulty difficulty,
        InterviewPhase phase,
        String content,
        String contentJapanese,
        int timeLimitSeconds,
        int orderIndex
) {}
