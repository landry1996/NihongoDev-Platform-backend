package com.nihongodev.platform.application.service.generator.sections;

import com.nihongodev.platform.application.service.generator.PitchSection;
import com.nihongodev.platform.domain.model.CvProfile;

public class MotivationSectionJP implements PitchSection {

    @Override
    public String render(CvProfile profile) {
        if (profile.getMotivationJapan() == null || profile.getMotivationJapan().isBlank()) {
            return "";
        }

        String framing = switch (profile.getTargetCompanyType()) {
            case STARTUP -> "日本のスタートアップで新しい技術に挑戦したいと思っています。";
            case ENTERPRISE -> "大規模なシステム開発に貢献したいと考えています。";
            case FOREIGN_IN_JAPAN -> "国際的なチームで働きながら日本の文化を学びたいです。";
            case TRADITIONAL_JAPANESE -> "日本のものづくりの精神を学び、品質の高いソフトウェアを作りたいです。";
        };

        return "## 志望動機\n\n" + framing + "\n" + profile.getMotivationJapan();
    }

    @Override
    public int order() { return 40; }
}
