package com.nihongodev.platform.infrastructure.web.request;

import com.nihongodev.platform.domain.model.PitchType;
import jakarta.validation.constraints.NotNull;

public record GeneratePitchRequest(
        @NotNull PitchType pitchType
) {}
