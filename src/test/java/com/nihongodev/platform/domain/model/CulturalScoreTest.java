package com.nihongodev.platform.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("CulturalScore Domain Model")
class CulturalScoreTest {

    @Test
    @DisplayName("should calculate correct weighted average (30%+25%+20%+15%+10%)")
    void shouldCalculateWeightedAverage() {
        CulturalScore score = CulturalScore.calculate(80, 70, 60, 50, 40);
        // 80*0.30 + 70*0.25 + 60*0.20 + 50*0.15 + 40*0.10 = 24+17.5+12+7.5+4 = 65
        assertThat(score.overallScore()).isEqualTo(65);
        assertThat(score.keigoScore()).isEqualTo(80);
        assertThat(score.appropriatenessScore()).isEqualTo(70);
        assertThat(score.uchiSotoScore()).isEqualTo(60);
        assertThat(score.indirectnessScore()).isEqualTo(50);
        assertThat(score.professionalToneScore()).isEqualTo(40);
    }

    @Test
    @DisplayName("should return all zeros from zero()")
    void shouldReturnAllZeros() {
        CulturalScore score = CulturalScore.zero();
        assertThat(score.keigoScore()).isEqualTo(0);
        assertThat(score.appropriatenessScore()).isEqualTo(0);
        assertThat(score.uchiSotoScore()).isEqualTo(0);
        assertThat(score.indirectnessScore()).isEqualTo(0);
        assertThat(score.professionalToneScore()).isEqualTo(0);
        assertThat(score.overallScore()).isEqualTo(0);
    }

    @Test
    @DisplayName("should return all 100s from perfect()")
    void shouldReturnPerfect() {
        CulturalScore score = CulturalScore.perfect();
        assertThat(score.keigoScore()).isEqualTo(100);
        assertThat(score.appropriatenessScore()).isEqualTo(100);
        assertThat(score.uchiSotoScore()).isEqualTo(100);
        assertThat(score.indirectnessScore()).isEqualTo(100);
        assertThat(score.professionalToneScore()).isEqualTo(100);
        assertThat(score.overallScore()).isEqualTo(100);
    }

    @Test
    @DisplayName("should calculate overall 0 when all inputs are 0")
    void shouldCalculateZeroWithAllZeroInputs() {
        CulturalScore score = CulturalScore.calculate(0, 0, 0, 0, 0);
        assertThat(score.overallScore()).isEqualTo(0);
    }

    @Test
    @DisplayName("should calculate overall 100 when all inputs are 100")
    void shouldCalculateHundredWithAllMaxInputs() {
        CulturalScore score = CulturalScore.calculate(100, 100, 100, 100, 100);
        assertThat(score.overallScore()).isEqualTo(100);
    }
}
