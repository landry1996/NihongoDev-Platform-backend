package com.nihongodev.platform.infrastructure.web.request;

import com.nihongodev.platform.domain.model.TargetCompanyType;
import jakarta.validation.constraints.*;

import java.util.List;

public record CreateCvProfileRequest(
        @NotBlank @Size(max = 200) String fullName,
        @Size(max = 200) String currentRole,
        @NotBlank @Size(max = 200) String targetRole,
        @Min(0) @Max(50) int yearsOfExperience,
        @NotNull TargetCompanyType targetCompanyType,
        @Size(max = 20) List<@Size(max = 50) String> techStack,
        @Size(max = 10) List<WorkExperienceData> experiences,
        @Size(max = 15) List<String> certifications,
        @Size(max = 5) List<String> notableProjects,
        @Size(max = 2000) String motivationJapan,
        String japaneseLevel
) {
    public record WorkExperienceData(
            @NotBlank String company,
            @NotBlank String role,
            @Min(1) int durationMonths,
            List<String> highlights
    ) {}
}
