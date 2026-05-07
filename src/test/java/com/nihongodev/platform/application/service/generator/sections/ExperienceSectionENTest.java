package com.nihongodev.platform.application.service.generator.sections;

import com.nihongodev.platform.domain.model.CvProfile;
import com.nihongodev.platform.domain.model.TargetCompanyType;
import com.nihongodev.platform.domain.model.WorkExperience;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ExperienceSectionEN")
class ExperienceSectionENTest {

    private final ExperienceSectionEN section = new ExperienceSectionEN();

    @Test
    @DisplayName("should render experience with highlights")
    void shouldRenderExperience() {
        CvProfile profile = CvProfile.create(UUID.randomUUID(), "Test", "Dev", 3, TargetCompanyType.STARTUP);
        profile.setExperiences(List.of(
                new WorkExperience("Google", "Backend Engineer", 30, List.of("Built API gateway", "Led migration"))
        ));

        String result = section.render(profile);

        assertThat(result).contains("## Professional Experience");
        assertThat(result).contains("Backend Engineer at Google");
        assertThat(result).contains("2 years 6 months");
        assertThat(result).contains("- Built API gateway");
        assertThat(result).contains("- Led migration");
    }

    @Test
    @DisplayName("should return empty for no experiences")
    void shouldReturnEmptyForNoExperiences() {
        CvProfile profile = CvProfile.create(UUID.randomUUID(), "Test", "Dev", 3, TargetCompanyType.STARTUP);

        String result = section.render(profile);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("should render multiple experiences")
    void shouldRenderMultipleExperiences() {
        CvProfile profile = CvProfile.create(UUID.randomUUID(), "Test", "Dev", 5, TargetCompanyType.ENTERPRISE);
        profile.setExperiences(List.of(
                new WorkExperience("Company A", "Senior Dev", 24, List.of("Led team")),
                new WorkExperience("Company B", "Junior Dev", 12, List.of("Learned fast"))
        ));

        String result = section.render(profile);

        assertThat(result).contains("Senior Dev at Company A");
        assertThat(result).contains("Junior Dev at Company B");
    }
}
