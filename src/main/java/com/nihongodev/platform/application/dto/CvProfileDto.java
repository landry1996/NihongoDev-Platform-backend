package com.nihongodev.platform.application.dto;

import com.nihongodev.platform.domain.model.TargetCompanyType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record CvProfileDto(
        UUID id,
        UUID userId,
        String fullName,
        String currentRole,
        String targetRole,
        int yearsOfExperience,
        TargetCompanyType targetCompanyType,
        List<String> techStack,
        List<WorkExperienceDto> experiences,
        List<String> certifications,
        List<String> notableProjects,
        String motivationJapan,
        String japaneseLevel,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
