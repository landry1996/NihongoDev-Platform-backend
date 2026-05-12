package com.nihongodev.platform.application.command;

import com.nihongodev.platform.domain.model.JapaneseLevel;
import com.nihongodev.platform.domain.model.ProfileVisibility;

import java.util.List;
import java.util.UUID;

public record CreatePublicProfileCommand(
    UUID userId,
    String displayName,
    String slug,
    String bio,
    JapaneseLevel currentLevel,
    ProfileVisibility visibility,
    boolean openToWork,
    String preferredRole,
    String location,
    List<String> highlightedSkills
) {}
