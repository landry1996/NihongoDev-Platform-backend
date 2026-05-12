package com.nihongodev.platform.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Badge")
class BadgeTest {

    @Test
    @DisplayName("should define badge with all fields")
    void shouldDefineBadge() {
        Badge badge = Badge.define("FIRST_LESSON", "初めの一歩", "First Step",
            BadgeCategory.MILESTONE, BadgeRarity.COMMON, 0, 1, 50);

        assertThat(badge.getId()).isNotNull();
        assertThat(badge.getCode()).isEqualTo("FIRST_LESSON");
        assertThat(badge.getNameJp()).isEqualTo("初めの一歩");
        assertThat(badge.getNameEn()).isEqualTo("First Step");
        assertThat(badge.getCategory()).isEqualTo(BadgeCategory.MILESTONE);
        assertThat(badge.getRarity()).isEqualTo(BadgeRarity.COMMON);
        assertThat(badge.getRequiredScore()).isZero();
        assertThat(badge.getRequiredCount()).isEqualTo(1);
        assertThat(badge.getXpReward()).isEqualTo(50);
    }
}
