package com.nihongodev.platform.application.dto;

import com.nihongodev.platform.domain.model.InterviewDifficulty;
import com.nihongodev.platform.domain.model.InterviewPhase;
import com.nihongodev.platform.domain.model.InterviewType;
import com.nihongodev.platform.domain.model.SessionStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record InterviewSessionDto(
        UUID id,
        InterviewType interviewType,
        InterviewDifficulty difficulty,
        InterviewPhase currentPhase,
        SessionStatus status,
        int currentQuestionIndex,
        int totalQuestions,
        double overallScore,
        double languageScore,
        double technicalScore,
        double communicationScore,
        double culturalScore,
        boolean passed,
        LocalDateTime startedAt,
        LocalDateTime completedAt
) {}
