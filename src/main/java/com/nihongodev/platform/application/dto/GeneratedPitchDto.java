package com.nihongodev.platform.application.dto;

import com.nihongodev.platform.domain.model.PitchType;

import java.time.LocalDateTime;
import java.util.UUID;

public record GeneratedPitchDto(
        UUID id,
        PitchType pitchType,
        String content,
        LocalDateTime generatedAt
) {}
