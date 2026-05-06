package com.nihongodev.platform.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("WeaknessPattern Domain Model")
class WeaknessPatternTest {

    @Test
    @DisplayName("should create with initial occurrence count of 1")
    void shouldCreateWithCountOne() {
        WeaknessPattern pattern = WeaknessPattern.create(
                UUID.randomUUID(), AnnotationCategory.GRAMMAR,
                "particle confusion", "はを間違えた");

        assertThat(pattern.getId()).isNotNull();
        assertThat(pattern.getOccurrenceCount()).isEqualTo(1);
        assertThat(pattern.isRecurring()).isFalse();
    }

    @Test
    @DisplayName("should increment occurrence and update example")
    void shouldIncrementOccurrence() {
        WeaknessPattern pattern = WeaknessPattern.create(
                UUID.randomUUID(), AnnotationCategory.KEIGO,
                "missing keigo", "first example");

        pattern.incrementOccurrence("second example");

        assertThat(pattern.getOccurrenceCount()).isEqualTo(2);
        assertThat(pattern.getLastExample()).isEqualTo("second example");
    }

    @Test
    @DisplayName("should mark as recurring when count >= 3")
    void shouldMarkRecurring() {
        WeaknessPattern pattern = WeaknessPattern.create(
                UUID.randomUUID(), AnnotationCategory.VOCABULARY,
                "casual vocabulary", "example");

        pattern.incrementOccurrence("ex2");
        assertThat(pattern.isRecurring()).isFalse();

        pattern.incrementOccurrence("ex3");
        assertThat(pattern.isRecurring()).isTrue();
    }
}
