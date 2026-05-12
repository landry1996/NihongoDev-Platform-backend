package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.dto.UserBadgeDto;
import com.nihongodev.platform.application.port.out.BadgeRepositoryPort;
import com.nihongodev.platform.application.port.out.EventPublisherPort;
import com.nihongodev.platform.application.port.out.UserBadgeRepositoryPort;
import com.nihongodev.platform.domain.model.Badge;
import com.nihongodev.platform.domain.model.BadgeCategory;
import com.nihongodev.platform.domain.model.BadgeRarity;
import com.nihongodev.platform.domain.model.UserBadge;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AwardBadgeUseCase")
class AwardBadgeUseCaseTest {

    @Mock private BadgeRepositoryPort badgeRepository;
    @Mock private UserBadgeRepositoryPort userBadgeRepository;
    @Mock private EventPublisherPort eventPublisher;

    private AwardBadgeUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new AwardBadgeUseCase(badgeRepository, userBadgeRepository, eventPublisher);
    }

    @Test
    @DisplayName("should award badge successfully")
    void shouldAwardBadge() {
        UUID userId = UUID.randomUUID();
        Badge badge = Badge.define("FIRST_LESSON", "初めの一歩", "First Step",
            BadgeCategory.MILESTONE, BadgeRarity.COMMON, 0, 1, 50);

        when(badgeRepository.findByCode("FIRST_LESSON")).thenReturn(Optional.of(badge));
        when(userBadgeRepository.existsByUserIdAndBadgeId(userId, badge.getId())).thenReturn(false);
        when(userBadgeRepository.save(any(UserBadge.class))).thenAnswer(inv -> inv.getArgument(0));

        UserBadgeDto dto = useCase.execute(userId, "FIRST_LESSON");

        assertThat(dto.badge().code()).isEqualTo("FIRST_LESSON");
        assertThat(dto.badge().nameJp()).isEqualTo("初めの一歩");
        assertThat(dto.showcased()).isFalse();
        verify(eventPublisher).publish(eq("badge-events"), any());
    }

    @Test
    @DisplayName("should fail if user already has badge")
    void shouldFailIfAlreadyHasBadge() {
        UUID userId = UUID.randomUUID();
        Badge badge = Badge.define("FIRST_LESSON", "初めの一歩", "First Step",
            BadgeCategory.MILESTONE, BadgeRarity.COMMON, 0, 1, 50);

        when(badgeRepository.findByCode("FIRST_LESSON")).thenReturn(Optional.of(badge));
        when(userBadgeRepository.existsByUserIdAndBadgeId(userId, badge.getId())).thenReturn(true);

        assertThatThrownBy(() -> useCase.execute(userId, "FIRST_LESSON"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("already has badge");
    }
}
