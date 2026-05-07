package com.nihongodev.platform.application.dto;

import java.util.List;

public record WorkExperienceDto(
        String company,
        String role,
        int durationMonths,
        List<String> highlights
) {}
