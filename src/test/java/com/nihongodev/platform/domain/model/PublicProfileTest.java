package com.nihongodev.platform.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("PublicProfile")
class PublicProfileTest {

    @Test
    @DisplayName("should create profile with correct defaults")
    void shouldCreateWithDefaults() {
        UUID userId = UUID.randomUUID();
        PublicProfile profile = PublicProfile.create(userId, "Taro Yamada", "taro-yamada");

        assertThat(profile.getId()).isNotNull();
        assertThat(profile.getUserId()).isEqualTo(userId);
        assertThat(profile.getDisplayName()).isEqualTo("Taro Yamada");
        assertThat(profile.getSlug()).isEqualTo("taro-yamada");
        assertThat(profile.getVisibility()).isEqualTo(ProfileVisibility.PUBLIC);
        assertThat(profile.isOpenToWork()).isFalse();
        assertThat(profile.getTotalXp()).isZero();
        assertThat(profile.getShowcasedBadgeIds()).isEmpty();
    }

    @Test
    @DisplayName("should toggle open to work")
    void shouldToggleOpenToWork() {
        PublicProfile profile = PublicProfile.create(UUID.randomUUID(), "Taro", "taro");

        assertThat(profile.isOpenToWork()).isFalse();
        profile.toggleOpenToWork();
        assertThat(profile.isOpenToWork()).isTrue();
        profile.toggleOpenToWork();
        assertThat(profile.isOpenToWork()).isFalse();
    }

    @Test
    @DisplayName("should showcase badge up to max 6")
    void shouldShowcaseBadgeWithLimit() {
        PublicProfile profile = PublicProfile.create(UUID.randomUUID(), "Taro", "taro");

        for (int i = 0; i < 6; i++) {
            profile.showcaseBadge(UUID.randomUUID());
        }
        assertThat(profile.getShowcasedBadgeIds()).hasSize(6);

        profile.showcaseBadge(UUID.randomUUID());
        assertThat(profile.getShowcasedBadgeIds()).hasSize(6);
    }

    @Test
    @DisplayName("should not duplicate showcased badge")
    void shouldNotDuplicateShowcasedBadge() {
        PublicProfile profile = PublicProfile.create(UUID.randomUUID(), "Taro", "taro");
        UUID badgeId = UUID.randomUUID();

        profile.showcaseBadge(badgeId);
        profile.showcaseBadge(badgeId);
        assertThat(profile.getShowcasedBadgeIds()).hasSize(1);
    }

    @Test
    @DisplayName("should remove showcased badge")
    void shouldRemoveShowcasedBadge() {
        PublicProfile profile = PublicProfile.create(UUID.randomUUID(), "Taro", "taro");
        UUID badgeId = UUID.randomUUID();

        profile.showcaseBadge(badgeId);
        assertThat(profile.getShowcasedBadgeIds()).contains(badgeId);

        profile.removeBadgeShowcase(badgeId);
        assertThat(profile.getShowcasedBadgeIds()).doesNotContain(badgeId);
    }

    @Test
    @DisplayName("should update stats")
    void shouldUpdateStats() {
        PublicProfile profile = PublicProfile.create(UUID.randomUUID(), "Taro", "taro");

        profile.updateStats(1500, 5, 20, 10, 87.5);

        assertThat(profile.getTotalXp()).isEqualTo(1500);
        assertThat(profile.getTotalBadges()).isEqualTo(5);
        assertThat(profile.getLessonsCompleted()).isEqualTo(20);
        assertThat(profile.getReadingSessionsCompleted()).isEqualTo(10);
        assertThat(profile.getAverageScore()).isEqualTo(87.5);
    }

    @Test
    @DisplayName("should check visibility correctly")
    void shouldCheckVisibility() {
        PublicProfile profile = PublicProfile.create(UUID.randomUUID(), "Taro", "taro");

        profile.setVisibility(ProfileVisibility.PUBLIC);
        assertThat(profile.isVisibleTo(false)).isTrue();
        assertThat(profile.isVisibleTo(true)).isTrue();

        profile.setVisibility(ProfileVisibility.RECRUITERS_ONLY);
        assertThat(profile.isVisibleTo(false)).isFalse();
        assertThat(profile.isVisibleTo(true)).isTrue();

        profile.setVisibility(ProfileVisibility.PRIVATE);
        assertThat(profile.isVisibleTo(false)).isFalse();
        assertThat(profile.isVisibleTo(true)).isFalse();
    }
}
