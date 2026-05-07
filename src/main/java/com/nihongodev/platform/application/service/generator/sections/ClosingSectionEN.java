package com.nihongodev.platform.application.service.generator.sections;

import com.nihongodev.platform.application.service.generator.PitchSection;
import com.nihongodev.platform.domain.model.CvProfile;

public class ClosingSectionEN implements PitchSection {

    @Override
    public String render(CvProfile profile) {
        String closing = switch (profile.getTargetCompanyType()) {
            case STARTUP -> "I'm excited to bring energy and fresh ideas to your team. Let's build something great together!";
            case ENTERPRISE -> "I look forward to contributing to your team's mission with reliability and technical depth.";
            case FOREIGN_IN_JAPAN -> "I'm ready to contribute across cultures and bring a global perspective to your team.";
            case TRADITIONAL_JAPANESE -> "I would be honored to contribute to your team and grow together. Thank you for your consideration.";
        };

        return "## Closing\n\n" + closing;
    }

    @Override
    public int order() { return 60; }
}
