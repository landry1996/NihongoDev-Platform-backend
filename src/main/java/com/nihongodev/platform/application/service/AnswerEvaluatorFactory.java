package com.nihongodev.platform.application.service;

import com.nihongodev.platform.domain.model.InterviewType;

import java.util.EnumMap;
import java.util.Map;

public final class AnswerEvaluatorFactory {

    private static final Map<InterviewType, AnswerEvaluator> EVALUATORS = new EnumMap<>(InterviewType.class);

    static {
        EVALUATORS.put(InterviewType.HR_JAPANESE, new KeywordBasedEvaluator());
        EVALUATORS.put(InterviewType.TECH_JAVA, new ReferenceAnswerEvaluator());
        EVALUATORS.put(InterviewType.TECH_SPRING, new ReferenceAnswerEvaluator());
        EVALUATORS.put(InterviewType.TECH_AWS, new ReferenceAnswerEvaluator());
        EVALUATORS.put(InterviewType.BEHAVIORAL, new StructureEvaluator());
        EVALUATORS.put(InterviewType.SELF_INTRODUCTION, new StructureEvaluator());
        EVALUATORS.put(InterviewType.BUSINESS_JAPANESE, new KeywordBasedEvaluator());
    }

    private AnswerEvaluatorFactory() {}

    public static AnswerEvaluator getEvaluator(InterviewType type) {
        return EVALUATORS.getOrDefault(type, new KeywordBasedEvaluator());
    }
}
