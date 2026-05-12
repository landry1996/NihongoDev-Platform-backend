package com.nihongodev.platform.infrastructure.web.request;

import com.nihongodev.platform.domain.model.ContentDomain;
import com.nihongodev.platform.domain.model.ContentSource;
import com.nihongodev.platform.domain.model.JapaneseLevel;
import com.nihongodev.platform.domain.model.ReadingDifficulty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record UpdateContentPreferencesRequest(
    @NotEmpty List<ContentDomain> preferredDomains,
    @NotNull JapaneseLevel currentLevel,
    @NotNull ReadingDifficulty maxDifficulty,
    @Min(1) int preferredReadingMinutes,
    @NotEmpty List<ContentSource> preferredSources
) {}
