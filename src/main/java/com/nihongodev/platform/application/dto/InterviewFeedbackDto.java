package com.nihongodev.platform.application.dto;

import java.util.List;
import java.util.UUID;

public record InterviewFeedbackDto(
        UUID answerId,
        double overallScore,
        double languageScore,
        double technicalScore,
        double communicationScore,
        double culturalScore,
        List<String> strengths,
        List<String> improvements,
        String modelAnswer,
        List<String> grammarNotes,
        List<String> vocabularySuggestions
) {}
