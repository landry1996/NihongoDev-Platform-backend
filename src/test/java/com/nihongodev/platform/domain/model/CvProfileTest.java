package com.nihongodev.platform.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("CvProfile")
class CvProfileTest {

    @Test
    @DisplayName("should create profile with factory method")
    void shouldCreateProfile() {
        UUID userId = UUID.randomUUID();
        CvProfile profile = CvProfile.create(userId, "Tanaka Taro", "Backend Engineer", 5, TargetCompanyType.STARTUP);

        assertThat(profile.getId()).isNotNull();
        assertThat(profile.getUserId()).isEqualTo(userId);
        assertThat(profile.getFullName()).isEqualTo("Tanaka Taro");
        assertThat(profile.getTargetRole()).isEqualTo("Backend Engineer");
        assertThat(profile.getYearsOfExperience()).isEqualTo(5);
        assertThat(profile.getTargetCompanyType()).isEqualTo(TargetCompanyType.STARTUP);
        assertThat(profile.getTechStack()).isEmpty();
        assertThat(profile.getExperiences()).isEmpty();
        assertThat(profile.getCertifications()).isEmpty();
        assertThat(profile.getNotableProjects()).isEmpty();
        assertThat(profile.getCreatedAt()).isNotNull();
        assertThat(profile.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("should add experience to profile")
    void shouldAddExperience() {
        CvProfile profile = CvProfile.create(UUID.randomUUID(), "Tanaka", "Backend", 3, TargetCompanyType.ENTERPRISE);
        WorkExperience exp = new WorkExperience("Google", "SWE", 24, List.of("Built microservices"));

        profile.setExperiences(List.of(exp));

        assertThat(profile.getExperiences()).hasSize(1);
        assertThat(profile.getExperiences().get(0).getCompany()).isEqualTo("Google");
    }

    @Test
    @DisplayName("should set tech stack")
    void shouldSetTechStack() {
        CvProfile profile = CvProfile.create(UUID.randomUUID(), "Tanaka", "Backend", 3, TargetCompanyType.ENTERPRISE);

        profile.setTechStack(List.of("Java", "Spring Boot", "PostgreSQL", "Kafka"));

        assertThat(profile.getTechStack()).containsExactly("Java", "Spring Boot", "PostgreSQL", "Kafka");
    }
}
