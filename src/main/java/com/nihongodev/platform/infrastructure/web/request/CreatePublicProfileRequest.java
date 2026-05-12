package com.nihongodev.platform.infrastructure.web.request;

import com.nihongodev.platform.domain.model.JapaneseLevel;
import com.nihongodev.platform.domain.model.ProfileVisibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CreatePublicProfileRequest(
    @NotBlank @Size(max = 100) String displayName,
    @NotBlank @Size(max = 100) String slug,
    @Size(max = 500) String bio,
    JapaneseLevel currentLevel,
    ProfileVisibility visibility,
    boolean openToWork,
    @Size(max = 100) String preferredRole,
    @Size(max = 100) String location,
    List<@Size(max = 50) String> highlightedSkills
) {}
