package com.nihongodev.platform.application.service;

import com.nihongodev.platform.domain.model.QuestionType;

import java.util.EnumMap;
import java.util.Map;

public final class QuestionCorrectorFactory {

    private static final Map<QuestionType, QuestionCorrector> CORRECTORS = new EnumMap<>(QuestionType.class);

    static {
        CORRECTORS.put(QuestionType.MULTIPLE_CHOICE, new MultipleChoiceCorrector());
        CORRECTORS.put(QuestionType.TRUE_FALSE, new TrueFalseCorrector());
        CORRECTORS.put(QuestionType.TEXT_INPUT, new TextInputCorrector());
        CORRECTORS.put(QuestionType.MATCHING, new MatchingCorrector());
    }

    private QuestionCorrectorFactory() {}

    public static QuestionCorrector getCorrector(QuestionType type) {
        return CORRECTORS.getOrDefault(type, new MultipleChoiceCorrector());
    }
}
