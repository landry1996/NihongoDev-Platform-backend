package com.nihongodev.platform.application.service.generator.sections;

import com.nihongodev.platform.application.service.generator.PitchSection;
import com.nihongodev.platform.domain.model.CvProfile;

public class IntroSectionEN implements PitchSection {

    @Override
    public String render(CvProfile profile) {
        String tone = switch (profile.getTargetCompanyType()) {
            case STARTUP -> "passionate about building innovative solutions";
            case ENTERPRISE -> "focused on delivering scalable and reliable systems";
            case FOREIGN_IN_JAPAN -> "adaptable and experienced in multicultural environments";
            case TRADITIONAL_JAPANESE -> "dedicated to quality and continuous improvement";
        };

        StringBuilder sb = new StringBuilder();
        sb.append("## Introduction\n\n");
        sb.append("Hi, I'm **").append(profile.getFullName()).append("**");
        if (profile.getCurrentRole() != null && !profile.getCurrentRole().isBlank()) {
            sb.append(", currently working as a **").append(profile.getCurrentRole()).append("**");
        }
        sb.append(". With **").append(profile.getYearsOfExperience()).append(" years** of experience");
        sb.append(", I am ").append(tone).append(".");
        sb.append("\n\nI'm looking to join as a **").append(profile.getTargetRole()).append("** in Japan.");

        return sb.toString();
    }

    @Override
    public int order() { return 10; }
}
