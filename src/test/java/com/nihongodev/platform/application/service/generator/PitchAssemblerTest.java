package com.nihongodev.platform.application.service.generator;

import com.nihongodev.platform.domain.model.CvProfile;
import com.nihongodev.platform.domain.model.PitchType;
import com.nihongodev.platform.domain.model.TargetCompanyType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("PitchAssembler")
class PitchAssemblerTest {

    @Test
    @DisplayName("should assemble sections in order")
    void shouldAssembleSectionsInOrder() {
        PitchSection first = new PitchSection() {
            @Override public String render(CvProfile profile) { return "## First\nContent A"; }
            @Override public int order() { return 1; }
        };
        PitchSection second = new PitchSection() {
            @Override public String render(CvProfile profile) { return "## Second\nContent B"; }
            @Override public int order() { return 2; }
        };

        PitchAssembler assembler = new PitchAssembler(Map.of(PitchType.ENGLISH_PITCH, List.of(second, first)));
        CvProfile profile = CvProfile.create(UUID.randomUUID(), "Test", "Dev", 3, TargetCompanyType.STARTUP);

        String result = assembler.assemble(profile, PitchType.ENGLISH_PITCH);

        assertThat(result).isEqualTo("## First\nContent A\n\n## Second\nContent B");
    }

    @Test
    @DisplayName("should skip empty sections")
    void shouldSkipEmptySections() {
        PitchSection content = new PitchSection() {
            @Override public String render(CvProfile profile) { return "## Hello"; }
            @Override public int order() { return 1; }
        };
        PitchSection empty = new PitchSection() {
            @Override public String render(CvProfile profile) { return ""; }
            @Override public int order() { return 2; }
        };

        PitchAssembler assembler = new PitchAssembler(Map.of(PitchType.ENGLISH_PITCH, List.of(content, empty)));
        CvProfile profile = CvProfile.create(UUID.randomUUID(), "Test", "Dev", 3, TargetCompanyType.STARTUP);

        String result = assembler.assemble(profile, PitchType.ENGLISH_PITCH);

        assertThat(result).isEqualTo("## Hello");
    }

    @Test
    @DisplayName("should throw for unknown pitch type")
    void shouldThrowForUnknownType() {
        PitchAssembler assembler = new PitchAssembler(Map.of());
        CvProfile profile = CvProfile.create(UUID.randomUUID(), "Test", "Dev", 3, TargetCompanyType.STARTUP);

        assertThatThrownBy(() -> assembler.assemble(profile, PitchType.ENGLISH_PITCH))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("No sections registered");
    }
}
