package com.nihongodev.platform.application.command;

import com.nihongodev.platform.domain.model.ContentDomain;
import com.nihongodev.platform.domain.model.ContentSource;
import com.nihongodev.platform.domain.model.JapaneseLevel;
import com.nihongodev.platform.domain.model.ReadingDifficulty;

import java.util.List;
import java.util.UUID;

public record UpdateContentPreferencesCommand(
    UUID userId,
    List<ContentDomain> preferredDomains,
    JapaneseLevel currentLevel,
    ReadingDifficulty maxDifficulty,
    int preferredReadingMinutes,
    List<ContentSource> preferredSources
) {}
