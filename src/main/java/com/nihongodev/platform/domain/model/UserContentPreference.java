package com.nihongodev.platform.domain.model;

import java.util.List;
import java.util.UUID;

public record UserContentPreference(
    UUID userId,
    List<ContentDomain> preferredDomains,
    JapaneseLevel currentLevel,
    ReadingDifficulty maxDifficulty,
    int preferredReadingMinutes,
    List<ContentSource> preferredSources
) {
    public static UserContentPreference defaults(UUID userId, JapaneseLevel level) {
        return new UserContentPreference(
            userId,
            List.of(ContentDomain.WEB_DEVELOPMENT, ContentDomain.GENERAL_SOFTWARE),
            level,
            ReadingDifficulty.INTERMEDIATE,
            10,
            List.of(ContentSource.TECH_BLOG, ContentSource.GITHUB_ISSUE)
        );
    }
}
