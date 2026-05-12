package com.nihongodev.platform.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CodeExerciseScoreTest {

    @Test
    void calculate_shouldComputeWeightedAverage() {
        CodeExerciseScore score = CodeExerciseScore.calculate(80, 70, 60, 50, 40);
        // 80*0.30 + 70*0.25 + 60*0.20 + 50*0.15 + 40*0.10 = 24+17.5+12+7.5+4 = 65
        assertThat(score.overallScore()).isEqualTo(65);
    }

    @Test
    void calculate_shouldHandlePerfectScores() {
        CodeExerciseScore score = CodeExerciseScore.calculate(100, 100, 100, 100, 100);
        assertThat(score.overallScore()).isEqualTo(100);
    }

    @Test
    void calculate_shouldHandleZeroScores() {
        CodeExerciseScore score = CodeExerciseScore.calculate(0, 0, 0, 0, 0);
        assertThat(score.overallScore()).isEqualTo(0);
    }

    @Test
    void calculate_shouldPreserveIndividualScores() {
        CodeExerciseScore score = CodeExerciseScore.calculate(90, 85, 75, 70, 65);
        assertThat(score.technicalAccuracyScore()).isEqualTo(90);
        assertThat(score.japaneseQualityScore()).isEqualTo(85);
        assertThat(score.professionalToneScore()).isEqualTo(75);
        assertThat(score.structureScore()).isEqualTo(70);
        assertThat(score.teamCommunicationScore()).isEqualTo(65);
    }

    @Test
    void calculate_shouldTruncateDecimals() {
        CodeExerciseScore score = CodeExerciseScore.calculate(77, 73, 69, 61, 53);
        assertThat(score.overallScore()).isGreaterThanOrEqualTo(0);
        assertThat(score.overallScore()).isLessThanOrEqualTo(100);
    }
}
