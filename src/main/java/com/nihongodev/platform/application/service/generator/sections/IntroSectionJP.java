package com.nihongodev.platform.application.service.generator.sections;

import com.nihongodev.platform.application.service.generator.PitchSection;
import com.nihongodev.platform.domain.model.CvProfile;

public class IntroSectionJP implements PitchSection {

    @Override
    public String render(CvProfile profile) {
        String greeting = switch (profile.getTargetCompanyType()) {
            case STARTUP, FOREIGN_IN_JAPAN -> "はじめまして。";
            case ENTERPRISE, TRADITIONAL_JAPANESE -> "はじめまして。お時間をいただきありがとうございます。";
        };

        StringBuilder sb = new StringBuilder();
        sb.append("## 自己紹介\n\n");
        sb.append(greeting).append("\n");
        sb.append("**").append(profile.getFullName()).append("**と申します。");
        sb.append("エンジニアとして**").append(profile.getYearsOfExperience()).append("年間**の経験があります。");
        sb.append("\n**").append(profile.getTargetRole()).append("**として働きたいと考えております。");

        if (profile.getJapaneseLevel() != null && !profile.getJapaneseLevel().isBlank()) {
            sb.append("\n日本語レベル: ").append(profile.getJapaneseLevel());
        }

        return sb.toString();
    }

    @Override
    public int order() { return 10; }
}
