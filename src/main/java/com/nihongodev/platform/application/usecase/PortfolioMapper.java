package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.dto.BadgeDto;
import com.nihongodev.platform.application.dto.PublicProfileDto;
import com.nihongodev.platform.application.dto.UserBadgeDto;
import com.nihongodev.platform.domain.model.Badge;
import com.nihongodev.platform.domain.model.PublicProfile;
import com.nihongodev.platform.domain.model.UserBadge;

import java.util.List;

public final class PortfolioMapper {

    private PortfolioMapper() {}

    public static PublicProfileDto toDto(PublicProfile profile, List<BadgeDto> showcasedBadges) {
        return new PublicProfileDto(
            profile.getId(),
            profile.getDisplayName(),
            profile.getSlug(),
            profile.getBio(),
            profile.getAvatarUrl(),
            profile.getCurrentLevel(),
            profile.getVisibility(),
            profile.isOpenToWork(),
            profile.getPreferredRole(),
            profile.getLocation(),
            profile.getTotalXp(),
            profile.getTotalBadges(),
            profile.getLessonsCompleted(),
            profile.getReadingSessionsCompleted(),
            profile.getAverageScore(),
            profile.getHighlightedSkills(),
            showcasedBadges,
            profile.getCreatedAt()
        );
    }

    public static BadgeDto toBadgeDto(Badge badge) {
        return new BadgeDto(
            badge.getId(),
            badge.getCode(),
            badge.getNameJp(),
            badge.getNameEn(),
            badge.getDescriptionJp(),
            badge.getDescriptionEn(),
            badge.getIconUrl(),
            badge.getCategory(),
            badge.getRarity(),
            badge.getRelatedModule(),
            badge.getXpReward()
        );
    }

    public static UserBadgeDto toUserBadgeDto(UserBadge userBadge, Badge badge) {
        return new UserBadgeDto(
            userBadge.getId(),
            toBadgeDto(badge),
            userBadge.getEarnedAt(),
            userBadge.isShowcased()
        );
    }
}
