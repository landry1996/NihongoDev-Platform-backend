package com.nihongodev.platform.infrastructure.web.request;

import com.nihongodev.platform.domain.model.TargetCompanyType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import java.util.List;

public record UpdateCvProfileRequest(
        @Size(max = 200) String fullName,
        @Size(max = 200) String currentRole,
        @Size(max = 200) String targetRole,
        @Min(0) @Max(50) Integer yearsOfExperience,
        TargetCompanyType targetCompanyType,
        @Size(max = 20) List<@Size(max = 50) String> techStack,
        @Size(max = 10) List<CreateCvProfileRequest.WorkExperienceData> experiences,
        @Size(max = 15) List<String> certifications,
        @Size(max = 5) List<String> notableProjects,
        @Size(max = 2000) String motivationJapan,
        String japaneseLevel
) {}
