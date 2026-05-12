package com.nihongodev.platform.application.port.in;

import com.nihongodev.platform.application.dto.GeneratedPitchDto;

import java.util.UUID;

public interface GenerateLlmCvPort {
    GeneratedPitchDto generate(UUID userId, String pitchType, String targetCompanyType, String additionalInstructions);
}
