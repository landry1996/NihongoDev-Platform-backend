package com.nihongodev.platform.application.dto;

import com.nihongodev.platform.domain.model.ContentDomain;
import com.nihongodev.platform.domain.model.ContentSource;
import com.nihongodev.platform.domain.model.JapaneseLevel;
import com.nihongodev.platform.domain.model.ReadingDifficulty;

import java.util.List;
import java.util.UUID;

public record UserContentPreferenceDto(
    UUID userId,
    List<ContentDomain> preferredDomains,
    JapaneseLevel currentLevel,
    ReadingDifficulty maxDifficulty,
    int preferredReadingMinutes,
    List<ContentSource> preferredSources
) {}
