package com.nihongodev.platform.application.service.generator.sections;

import com.nihongodev.platform.domain.model.CvProfile;
import com.nihongodev.platform.domain.model.TargetCompanyType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("TechStackSection")
class TechStackSectionTest {

    private final TechStackSection section = new TechStackSection();

    @Test
    @DisplayName("should render tech stack as pipe-separated list")
    void shouldRenderTechStack() {
        CvProfile profile = CvProfile.create(UUID.randomUUID(), "Test", "Dev", 3, TargetCompanyType.STARTUP);
        profile.setTechStack(List.of("Java", "Spring Boot", "PostgreSQL"));

        String result = section.render(profile);

        assertThat(result).contains("## Technical Skills");
        assertThat(result).contains("Java | Spring Boot | PostgreSQL");
    }

    @Test
    @DisplayName("should return empty for empty tech stack")
    void shouldReturnEmptyForNoStack() {
        CvProfile profile = CvProfile.create(UUID.randomUUID(), "Test", "Dev", 3, TargetCompanyType.STARTUP);

        String result = section.render(profile);

        assertThat(result).isEmpty();
    }
}
