package com.nihongodev.platform.application.command;

import com.nihongodev.platform.domain.model.JapaneseLevel;
import com.nihongodev.platform.domain.model.ProfileVisibility;

import java.util.List;
import java.util.UUID;

public record UpdatePublicProfileCommand(
    UUID userId,
    String displayName,
    String bio,
    JapaneseLevel currentLevel,
    ProfileVisibility visibility,
    boolean openToWork,
    String preferredRole,
    String location,
    List<String> highlightedSkills
) {}
