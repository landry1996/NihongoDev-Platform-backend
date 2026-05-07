package com.nihongodev.platform.application.service.generator.sections;

import com.nihongodev.platform.domain.model.CvProfile;
import com.nihongodev.platform.domain.model.TargetCompanyType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("IntroSectionEN")
class IntroSectionENTest {

    private final IntroSectionEN section = new IntroSectionEN();

    @Test
    @DisplayName("should render full intro with current role")
    void shouldRenderFullIntro() {
        CvProfile profile = CvProfile.create(UUID.randomUUID(), "Pierre Tchiengue", "Backend Engineer", 5, TargetCompanyType.STARTUP);
        profile.setCurrentRole("Java Developer");

        String result = section.render(profile);

        assertThat(result).contains("**Pierre Tchiengue**");
        assertThat(result).contains("**Java Developer**");
        assertThat(result).contains("**5 years**");
        assertThat(result).contains("innovative");
        assertThat(result).contains("**Backend Engineer**");
    }

    @Test
    @DisplayName("should adapt tone for enterprise")
    void shouldAdaptForEnterprise() {
        CvProfile profile = CvProfile.create(UUID.randomUUID(), "Tanaka", "SRE", 8, TargetCompanyType.ENTERPRISE);

        String result = section.render(profile);

        assertThat(result).contains("scalable and reliable");
    }

    @Test
    @DisplayName("should adapt tone for traditional Japanese")
    void shouldAdaptForTraditionalJapanese() {
        CvProfile profile = CvProfile.create(UUID.randomUUID(), "Tanaka", "Dev", 3, TargetCompanyType.TRADITIONAL_JAPANESE);

        String result = section.render(profile);

        assertThat(result).contains("quality and continuous improvement");
    }

    @Test
    @DisplayName("should handle missing current role")
    void shouldHandleMissingCurrentRole() {
        CvProfile profile = CvProfile.create(UUID.randomUUID(), "Tanaka", "Dev", 2, TargetCompanyType.FOREIGN_IN_JAPAN);

        String result = section.render(profile);

        assertThat(result).doesNotContain("currently working as");
        assertThat(result).contains("multicultural");
    }
}
