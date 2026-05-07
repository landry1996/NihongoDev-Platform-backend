package com.nihongodev.platform.application.dto;

import com.nihongodev.platform.domain.model.ProgressLevel;
import java.util.UUID;

public record TopUserDto(
        UUID userId,
        String displayName,
        long totalXp,
        double globalScore,
        ProgressLevel level
) {}
