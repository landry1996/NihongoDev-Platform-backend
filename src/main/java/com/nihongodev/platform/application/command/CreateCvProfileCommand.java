package com.nihongodev.platform.application.command;

import com.nihongodev.platform.domain.model.TargetCompanyType;

import java.util.List;

public record CreateCvProfileCommand(
        String fullName,
        String currentRole,
        String targetRole,
        int yearsOfExperience,
        TargetCompanyType targetCompanyType,
        List<String> techStack,
        List<WorkExperienceData> experiences,
        List<String> certifications,
        List<String> notableProjects,
        String motivationJapan,
        String japaneseLevel
) {
    public record WorkExperienceData(
            String company,
            String role,
            int durationMonths,
            List<String> highlights
    ) {}
}
