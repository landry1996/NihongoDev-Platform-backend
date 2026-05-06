package com.nihongodev.platform.application.service;

import com.nihongodev.platform.domain.model.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("AnswerEvaluatorFactory")
class AnswerEvaluatorFactoryTest {

    @Test
    @DisplayName("should return KeywordBasedEvaluator for HR_JAPANESE")
    void shouldReturnKeywordForHR() {
        AnswerEvaluator evaluator = AnswerEvaluatorFactory.getEvaluator(InterviewType.HR_JAPANESE);
        assertThat(evaluator).isInstanceOf(KeywordBasedEvaluator.class);
    }

    @Test
    @DisplayName("should return ReferenceAnswerEvaluator for TECH_JAVA")
    void shouldReturnReferenceForTechJava() {
        AnswerEvaluator evaluator = AnswerEvaluatorFactory.getEvaluator(InterviewType.TECH_JAVA);
        assertThat(evaluator).isInstanceOf(ReferenceAnswerEvaluator.class);
    }

    @Test
    @DisplayName("should return ReferenceAnswerEvaluator for TECH_SPRING")
    void shouldReturnReferenceForTechSpring() {
        AnswerEvaluator evaluator = AnswerEvaluatorFactory.getEvaluator(InterviewType.TECH_SPRING);
        assertThat(evaluator).isInstanceOf(ReferenceAnswerEvaluator.class);
    }

    @Test
    @DisplayName("should return StructureEvaluator for BEHAVIORAL")
    void shouldReturnStructureForBehavioral() {
        AnswerEvaluator evaluator = AnswerEvaluatorFactory.getEvaluator(InterviewType.BEHAVIORAL);
        assertThat(evaluator).isInstanceOf(StructureEvaluator.class);
    }

    @Test
    @DisplayName("should return StructureEvaluator for SELF_INTRODUCTION")
    void shouldReturnStructureForSelfIntro() {
        AnswerEvaluator evaluator = AnswerEvaluatorFactory.getEvaluator(InterviewType.SELF_INTRODUCTION);
        assertThat(evaluator).isInstanceOf(StructureEvaluator.class);
    }

    @Test
    @DisplayName("should return KeywordBasedEvaluator for BUSINESS_JAPANESE")
    void shouldReturnKeywordForBusiness() {
        AnswerEvaluator evaluator = AnswerEvaluatorFactory.getEvaluator(InterviewType.BUSINESS_JAPANESE);
        assertThat(evaluator).isInstanceOf(KeywordBasedEvaluator.class);
    }

    @Test
    @DisplayName("should score higher when expected keywords are present")
    void shouldScoreHigherWithKeywords() {
        InterviewQuestion question = InterviewQuestion.create(
                InterviewType.HR_JAPANESE, InterviewDifficulty.BEGINNER, InterviewPhase.MAIN_QUESTIONS,
                "Motivation?", "志望動機は？", "model answer",
                List.of("御社", "技術", "成長"), "criteria", 120, 1
        );

        AnswerEvaluator evaluator = AnswerEvaluatorFactory.getEvaluator(InterviewType.HR_JAPANESE);

        InterviewFeedback withKeywords = evaluator.evaluate(question, "御社の技術力と成長環境に魅力を感じております。", 40);
        InterviewFeedback withoutKeywords = evaluator.evaluate(question, "I want to work here because it seems nice.", 40);

        assertThat(withKeywords.getTechnicalScore()).isGreaterThan(withoutKeywords.getTechnicalScore());
    }

    @Test
    @DisplayName("should detect STAR structure in behavioral answers")
    void shouldDetectSTARStructure() {
        InterviewQuestion question = InterviewQuestion.create(
                InterviewType.BEHAVIORAL, InterviewDifficulty.INTERMEDIATE, InterviewPhase.MAIN_QUESTIONS,
                "Difficult problem?", "困難な問題は？", "model",
                List.of("状況", "行動", "結果"), "STAR", 180, 1
        );

        AnswerEvaluator evaluator = AnswerEvaluatorFactory.getEvaluator(InterviewType.BEHAVIORAL);

        InterviewFeedback starAnswer = evaluator.evaluate(question,
                "当時の状況は複雑でした。課題として、チームの協力が必要でした。行動として、会議を開催しました。結果として、プロジェクトは成功しました。", 90);
        InterviewFeedback shortAnswer = evaluator.evaluate(question, "I solved it.", 5);

        assertThat(starAnswer.getTechnicalScore()).isGreaterThan(shortAnswer.getTechnicalScore());
    }
}
