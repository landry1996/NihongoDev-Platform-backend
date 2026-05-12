package com.nihongodev.platform.application.dto;

import com.nihongodev.platform.domain.model.JapaneseLevel;
import com.nihongodev.platform.domain.model.ProfileVisibility;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record PublicProfileDto(
    UUID id,
    String displayName,
    String slug,
    String bio,
    String avatarUrl,
    JapaneseLevel currentLevel,
    ProfileVisibility visibility,
    boolean openToWork,
    String preferredRole,
    String location,
    int totalXp,
    int totalBadges,
    int lessonsCompleted,
    int readingSessionsCompleted,
    double averageScore,
    List<String> highlightedSkills,
    List<BadgeDto> showcasedBadges,
    LocalDateTime createdAt
) {}
