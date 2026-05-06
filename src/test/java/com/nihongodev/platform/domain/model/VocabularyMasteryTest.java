package com.nihongodev.platform.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("VocabularyMastery — SRS Algorithm")
class VocabularyMasteryTest {

    @Test
    @DisplayName("should start as NEW with default values")
    void shouldStartAsNew() {
        VocabularyMastery m = VocabularyMastery.create(UUID.randomUUID(), UUID.randomUUID());

        assertThat(m.getMasteryLevel()).isEqualTo(MasteryLevel.NEW);
        assertThat(m.getEaseFactor()).isEqualTo(2.5);
        assertThat(m.getIntervalDays()).isEqualTo(0);
        assertThat(m.getRepetitions()).isEqualTo(0);
    }

    @Test
    @DisplayName("should advance to LEARNING after first correct review")
    void shouldAdvanceToLearning() {
        VocabularyMastery m = VocabularyMastery.create(UUID.randomUUID(), UUID.randomUUID());
        m.recordReview(true);

        assertThat(m.getMasteryLevel()).isEqualTo(MasteryLevel.LEARNING);
        assertThat(m.getRepetitions()).isEqualTo(1);
        assertThat(m.getIntervalDays()).isEqualTo(1);
        assertThat(m.getCorrectCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("should set interval to 6 days after second correct review")
    void shouldSetSixDayInterval() {
        VocabularyMastery m = VocabularyMastery.create(UUID.randomUUID(), UUID.randomUUID());
        m.recordReview(true);
        m.recordReview(true);

        assertThat(m.getRepetitions()).isEqualTo(2);
        assertThat(m.getIntervalDays()).isEqualTo(6);
        assertThat(m.getMasteryLevel()).isEqualTo(MasteryLevel.LEARNING);
    }

    @Test
    @DisplayName("should advance to REVIEWING after third correct review")
    void shouldAdvanceToReviewing() {
        VocabularyMastery m = VocabularyMastery.create(UUID.randomUUID(), UUID.randomUUID());
        m.recordReview(true);
        m.recordReview(true);
        m.recordReview(true);

        assertThat(m.getMasteryLevel()).isEqualTo(MasteryLevel.REVIEWING);
        assertThat(m.getRepetitions()).isEqualTo(3);
        assertThat(m.getIntervalDays()).isGreaterThan(6);
    }

    @Test
    @DisplayName("should reset to NEW on incorrect review")
    void shouldResetOnIncorrect() {
        VocabularyMastery m = VocabularyMastery.create(UUID.randomUUID(), UUID.randomUUID());
        m.recordReview(true);
        m.recordReview(true);
        m.recordReview(false);

        assertThat(m.getMasteryLevel()).isEqualTo(MasteryLevel.NEW);
        assertThat(m.getRepetitions()).isEqualTo(0);
        assertThat(m.getIntervalDays()).isEqualTo(1);
        assertThat(m.getIncorrectCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("should decrease ease factor on incorrect")
    void shouldDecreaseEaseOnIncorrect() {
        VocabularyMastery m = VocabularyMastery.create(UUID.randomUUID(), UUID.randomUUID());
        double initialEase = m.getEaseFactor();
        m.recordReview(false);

        assertThat(m.getEaseFactor()).isLessThan(initialEase);
        assertThat(m.getEaseFactor()).isGreaterThanOrEqualTo(1.3);
    }
}
