package com.nihongodev.platform.application.dto;

import java.util.List;

public record ContentFeedDto(
    List<RealContentDto> recommended,
    List<RealContentDto> newArrivals,
    int totalAvailable
) {}
