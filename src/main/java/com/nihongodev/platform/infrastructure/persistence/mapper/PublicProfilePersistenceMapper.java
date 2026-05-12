package com.nihongodev.platform.infrastructure.persistence.mapper;

import com.nihongodev.platform.domain.model.JapaneseLevel;
import com.nihongodev.platform.domain.model.ProfileVisibility;
import com.nihongodev.platform.domain.model.PublicProfile;
import com.nihongodev.platform.infrastructure.persistence.entity.PublicProfileEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class PublicProfilePersistenceMapper {

    public PublicProfile toDomain(PublicProfileEntity entity) {
        PublicProfile profile = new PublicProfile();
        profile.setId(entity.getId());
        profile.setUserId(entity.getUserId());
        profile.setDisplayName(entity.getDisplayName());
        profile.setSlug(entity.getSlug());
        profile.setBio(entity.getBio());
        profile.setAvatarUrl(entity.getAvatarUrl());
        if (entity.getCurrentLevel() != null) {
            profile.setCurrentLevel(JapaneseLevel.valueOf(entity.getCurrentLevel()));
        }
        profile.setVisibility(ProfileVisibility.valueOf(entity.getVisibility()));
        profile.setOpenToWork(entity.isOpenToWork());
        profile.setPreferredRole(entity.getPreferredRole());
        profile.setLocation(entity.getLocation());
        profile.setTotalXp(entity.getTotalXp());
        profile.setTotalBadges(entity.getTotalBadges());
        profile.setLessonsCompleted(entity.getLessonsCompleted());
        profile.setReadingSessionsCompleted(entity.getReadingSessionsCompleted());
        profile.setAverageScore(entity.getAverageScore());
        profile.setHighlightedSkills(entity.getHighlightedSkills() != null
            ? new ArrayList<>(entity.getHighlightedSkills()) : new ArrayList<>());
        if (entity.getShowcasedBadgeIds() != null) {
            List<UUID> badgeIds = entity.getShowcasedBadgeIds().stream()
                .map(UUID::fromString)
                .toList();
            profile.setShowcasedBadgeIds(new ArrayList<>(badgeIds));
        } else {
            profile.setShowcasedBadgeIds(new ArrayList<>());
        }
        profile.setCreatedAt(entity.getCreatedAt());
        profile.setUpdatedAt(entity.getUpdatedAt());
        return profile;
    }

    public PublicProfileEntity toEntity(PublicProfile profile) {
        PublicProfileEntity entity = new PublicProfileEntity();
        entity.setId(profile.getId());
        entity.setUserId(profile.getUserId());
        entity.setDisplayName(profile.getDisplayName());
        entity.setSlug(profile.getSlug());
        entity.setBio(profile.getBio());
        entity.setAvatarUrl(profile.getAvatarUrl());
        if (profile.getCurrentLevel() != null) {
            entity.setCurrentLevel(profile.getCurrentLevel().name());
        }
        entity.setVisibility(profile.getVisibility().name());
        entity.setOpenToWork(profile.isOpenToWork());
        entity.setPreferredRole(profile.getPreferredRole());
        entity.setLocation(profile.getLocation());
        entity.setTotalXp(profile.getTotalXp());
        entity.setTotalBadges(profile.getTotalBadges());
        entity.setLessonsCompleted(profile.getLessonsCompleted());
        entity.setReadingSessionsCompleted(profile.getReadingSessionsCompleted());
        entity.setAverageScore(profile.getAverageScore());
        entity.setHighlightedSkills(profile.getHighlightedSkills() != null
            ? new ArrayList<>(profile.getHighlightedSkills()) : new ArrayList<>());
        if (profile.getShowcasedBadgeIds() != null) {
            List<String> badgeIds = profile.getShowcasedBadgeIds().stream()
                .map(UUID::toString)
                .toList();
            entity.setShowcasedBadgeIds(new ArrayList<>(badgeIds));
        } else {
            entity.setShowcasedBadgeIds(new ArrayList<>());
        }
        entity.setCreatedAt(profile.getCreatedAt());
        entity.setUpdatedAt(profile.getUpdatedAt());
        return entity;
    }
}
