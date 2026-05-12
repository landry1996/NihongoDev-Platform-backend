package com.nihongodev.platform.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("UserBadge")
class UserBadgeTest {

    @Test
    @DisplayName("should award badge with correct defaults")
    void shouldAwardBadge() {
        UUID userId = UUID.randomUUID();
        UUID badgeId = UUID.randomUUID();

        UserBadge userBadge = UserBadge.award(userId, badgeId);

        assertThat(userBadge.getId()).isNotNull();
        assertThat(userBadge.getUserId()).isEqualTo(userId);
        assertThat(userBadge.getBadgeId()).isEqualTo(badgeId);
        assertThat(userBadge.getEarnedAt()).isNotNull();
        assertThat(userBadge.isShowcased()).isFalse();
    }

    @Test
    @DisplayName("should toggle showcase state")
    void shouldToggleShowcase() {
        UserBadge userBadge = UserBadge.award(UUID.randomUUID(), UUID.randomUUID());

        assertThat(userBadge.isShowcased()).isFalse();
        userBadge.showcase();
        assertThat(userBadge.isShowcased()).isTrue();
        userBadge.unshowcase();
        assertThat(userBadge.isShowcased()).isFalse();
    }
}
