package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.port.out.PublicProfileRepositoryPort;
import com.nihongodev.platform.application.port.out.UserBadgeRepositoryPort;
import com.nihongodev.platform.domain.model.PublicProfile;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ShowcaseBadgeUseCase")
class ShowcaseBadgeUseCaseTest {

    @Mock private UserBadgeRepositoryPort userBadgeRepository;
    @Mock private PublicProfileRepositoryPort profileRepository;

    private ShowcaseBadgeUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new ShowcaseBadgeUseCase(userBadgeRepository, profileRepository);
    }

    @Test
    @DisplayName("should showcase badge and update profile")
    void shouldShowcaseBadge() {
        UUID userId = UUID.randomUUID();
        UUID badgeId = UUID.randomUUID();
        UserBadge userBadge = UserBadge.award(userId, badgeId);
        PublicProfile profile = PublicProfile.create(userId, "Taro", "taro");

        when(userBadgeRepository.findByUserIdAndBadgeId(userId, badgeId))
            .thenReturn(Optional.of(userBadge));
        when(userBadgeRepository.save(any(UserBadge.class))).thenAnswer(inv -> inv.getArgument(0));
        when(profileRepository.findByUserId(userId)).thenReturn(Optional.of(profile));
        when(profileRepository.save(any(PublicProfile.class))).thenAnswer(inv -> inv.getArgument(0));

        useCase.showcase(userId, badgeId);

        assertThat(userBadge.isShowcased()).isTrue();
        assertThat(profile.getShowcasedBadgeIds()).contains(badgeId);
        verify(profileRepository).save(profile);
    }

    @Test
    @DisplayName("should unshowcase badge and update profile")
    void shouldUnshowcaseBadge() {
        UUID userId = UUID.randomUUID();
        UUID badgeId = UUID.randomUUID();
        UserBadge userBadge = UserBadge.award(userId, badgeId);
        userBadge.showcase();
        PublicProfile profile = PublicProfile.create(userId, "Taro", "taro");
        profile.showcaseBadge(badgeId);

        when(userBadgeRepository.findByUserIdAndBadgeId(userId, badgeId))
            .thenReturn(Optional.of(userBadge));
        when(userBadgeRepository.save(any(UserBadge.class))).thenAnswer(inv -> inv.getArgument(0));
        when(profileRepository.findByUserId(userId)).thenReturn(Optional.of(profile));
        when(profileRepository.save(any(PublicProfile.class))).thenAnswer(inv -> inv.getArgument(0));

        useCase.unshowcase(userId, badgeId);

        assertThat(userBadge.isShowcased()).isFalse();
        assertThat(profile.getShowcasedBadgeIds()).doesNotContain(badgeId);
    }
}
