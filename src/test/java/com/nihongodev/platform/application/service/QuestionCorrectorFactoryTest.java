package com.nihongodev.platform.application.service;

import com.nihongodev.platform.domain.model.DifficultyLevel;
import com.nihongodev.platform.domain.model.Question;
import com.nihongodev.platform.domain.model.QuestionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("QuestionCorrectorFactory")
class QuestionCorrectorFactoryTest {

    @Test
    @DisplayName("should return MultipleChoiceCorrector for MULTIPLE_CHOICE")
    void shouldReturnMultipleChoiceCorrector() {
        QuestionCorrector corrector = QuestionCorrectorFactory.getCorrector(QuestionType.MULTIPLE_CHOICE);
        assertThat(corrector).isInstanceOf(MultipleChoiceCorrector.class);
    }

    @Test
    @DisplayName("should return TrueFalseCorrector for TRUE_FALSE")
    void shouldReturnTrueFalseCorrector() {
        QuestionCorrector corrector = QuestionCorrectorFactory.getCorrector(QuestionType.TRUE_FALSE);
        assertThat(corrector).isInstanceOf(TrueFalseCorrector.class);
    }

    @Test
    @DisplayName("should return TextInputCorrector for TEXT_INPUT")
    void shouldReturnTextInputCorrector() {
        QuestionCorrector corrector = QuestionCorrectorFactory.getCorrector(QuestionType.TEXT_INPUT);
        assertThat(corrector).isInstanceOf(TextInputCorrector.class);
    }

    @Test
    @DisplayName("should return MatchingCorrector for MATCHING")
    void shouldReturnMatchingCorrector() {
        QuestionCorrector corrector = QuestionCorrectorFactory.getCorrector(QuestionType.MATCHING);
        assertThat(corrector).isInstanceOf(MatchingCorrector.class);
    }

    @Test
    @DisplayName("should correctly evaluate multiple choice answer case-insensitively")
    void shouldCorrectMultipleChoiceCaseInsensitive() {
        Question question = Question.create(UUID.randomUUID(), "What is 配列?",
                "array", "配列 means array",
                QuestionType.MULTIPLE_CHOICE, DifficultyLevel.EASY,
                List.of("array", "list", "map", "set"), 1, 30, 1);

        QuestionCorrector corrector = QuestionCorrectorFactory.getCorrector(QuestionType.MULTIPLE_CHOICE);
        assertThat(corrector.correct(question, "Array")).isTrue();
        assertThat(corrector.correct(question, "ARRAY")).isTrue();
        assertThat(corrector.correct(question, "list")).isFalse();
    }

    @Test
    @DisplayName("should correctly evaluate true/false answer")
    void shouldCorrectTrueFalse() {
        Question question = Question.create(UUID.randomUUID(), "インターフェース means interface?",
                "true", "Correct",
                QuestionType.TRUE_FALSE, DifficultyLevel.EASY,
                List.of("true", "false"), 1, 15, 1);

        QuestionCorrector corrector = QuestionCorrectorFactory.getCorrector(QuestionType.TRUE_FALSE);
        assertThat(corrector.correct(question, "True")).isTrue();
        assertThat(corrector.correct(question, "FALSE")).isFalse();
    }

    @Test
    @DisplayName("should correctly evaluate text input with whitespace normalization")
    void shouldCorrectTextInputNormalized() {
        Question question = Question.create(UUID.randomUUID(), "Translate: コンパイラ",
                "compiler", "コンパイラ means compiler",
                QuestionType.TEXT_INPUT, DifficultyLevel.MEDIUM,
                List.of(), 2, 45, 1);

        QuestionCorrector corrector = QuestionCorrectorFactory.getCorrector(QuestionType.TEXT_INPUT);
        assertThat(corrector.correct(question, "compiler")).isTrue();
        assertThat(corrector.correct(question, " Compiler ")).isTrue();
        assertThat(corrector.correct(question, "COMPILER")).isTrue();
        assertThat(corrector.correct(question, "compile")).isFalse();
    }
}
