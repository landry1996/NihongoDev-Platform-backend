package com.nihongodev.platform.application.service.generator.sections;

import com.nihongodev.platform.application.service.generator.PitchSection;
import com.nihongodev.platform.domain.model.CvProfile;

public class MotivationSectionEN implements PitchSection {

    @Override
    public String render(CvProfile profile) {
        if (profile.getMotivationJapan() == null || profile.getMotivationJapan().isBlank()) {
            return "";
        }

        String framing = switch (profile.getTargetCompanyType()) {
            case STARTUP -> "I'm drawn to Japan's innovative startup ecosystem.";
            case ENTERPRISE -> "I'm attracted by the engineering culture at scale in Japan.";
            case FOREIGN_IN_JAPAN -> "I want to bridge international collaboration in Japan's tech scene.";
            case TRADITIONAL_JAPANESE -> "I deeply respect Japanese craftsmanship and dedication to quality.";
        };

        return "## Why Japan\n\n" + framing + " " + profile.getMotivationJapan();
    }

    @Override
    public int order() { return 40; }
}
