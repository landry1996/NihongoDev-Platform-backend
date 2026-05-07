package com.nihongodev.platform.application.service.generator.sections;

import com.nihongodev.platform.domain.model.CvProfile;
import com.nihongodev.platform.domain.model.TargetCompanyType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("IntroSectionJP")
class IntroSectionJPTest {

    private final IntroSectionJP section = new IntroSectionJP();

    @Test
    @DisplayName("should render Japanese jikoshoukai")
    void shouldRenderJikoshoukai() {
        CvProfile profile = CvProfile.create(UUID.randomUUID(), "Pierre", "Backend Engineer", 5, TargetCompanyType.STARTUP);
        profile.setJapaneseLevel("N3");

        String result = section.render(profile);

        assertThat(result).contains("## 自己紹介");
        assertThat(result).contains("**Pierre**と申します");
        assertThat(result).contains("**5年間**");
        assertThat(result).contains("**Backend Engineer**");
        assertThat(result).contains("日本語レベル: N3");
    }

    @Test
    @DisplayName("should use formal greeting for traditional company")
    void shouldUseFormalGreeting() {
        CvProfile profile = CvProfile.create(UUID.randomUUID(), "Tanaka", "Dev", 3, TargetCompanyType.TRADITIONAL_JAPANESE);

        String result = section.render(profile);

        assertThat(result).contains("お時間をいただきありがとうございます");
    }

    @Test
    @DisplayName("should use casual greeting for startup")
    void shouldUseCasualGreeting() {
        CvProfile profile = CvProfile.create(UUID.randomUUID(), "Tanaka", "Dev", 3, TargetCompanyType.STARTUP);

        String result = section.render(profile);

        assertThat(result).contains("はじめまして。");
        assertThat(result).doesNotContain("お時間をいただき");
    }

    @Test
    @DisplayName("should skip japanese level if not set")
    void shouldSkipJapaneseLevelIfNotSet() {
        CvProfile profile = CvProfile.create(UUID.randomUUID(), "Tanaka", "Dev", 3, TargetCompanyType.STARTUP);

        String result = section.render(profile);

        assertThat(result).doesNotContain("日本語レベル");
    }
}
