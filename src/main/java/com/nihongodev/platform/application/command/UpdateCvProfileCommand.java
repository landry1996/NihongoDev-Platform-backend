package com.nihongodev.platform.application.command;

import com.nihongodev.platform.domain.model.TargetCompanyType;

import java.util.List;

public record UpdateCvProfileCommand(
        String fullName,
        String currentRole,
        String targetRole,
        Integer yearsOfExperience,
        TargetCompanyType targetCompanyType,
        List<String> techStack,
        List<CreateCvProfileCommand.WorkExperienceData> experiences,
        List<String> certifications,
        List<String> notableProjects,
        String motivationJapan,
        String japaneseLevel
) {}
