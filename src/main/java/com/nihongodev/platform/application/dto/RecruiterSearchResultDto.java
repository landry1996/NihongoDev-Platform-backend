package com.nihongodev.platform.application.dto;

import com.nihongodev.platform.domain.model.JapaneseLevel;

import java.util.List;

public record RecruiterSearchResultDto(
    List<PublicProfileDto> profiles,
    int totalResults,
    int page,
    int pageSize
) {}
