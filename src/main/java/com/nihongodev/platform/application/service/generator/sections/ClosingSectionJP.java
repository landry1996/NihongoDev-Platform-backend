package com.nihongodev.platform.application.service.generator.sections;

import com.nihongodev.platform.application.service.generator.PitchSection;
import com.nihongodev.platform.domain.model.CvProfile;

public class ClosingSectionJP implements PitchSection {

    @Override
    public String render(CvProfile profile) {
        String closing = switch (profile.getTargetCompanyType()) {
            case STARTUP, FOREIGN_IN_JAPAN -> "ぜひ一緒に働かせていただければ嬉しいです。よろしくお願いいたします。";
            case ENTERPRISE, TRADITIONAL_JAPANESE -> "御社に貢献できるよう努力してまいります。何卒よろしくお願い申し上げます。";
        };

        return "## おわりに\n\n" + closing;
    }

    @Override
    public int order() { return 60; }
}
