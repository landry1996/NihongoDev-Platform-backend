package com.nihongodev.platform.infrastructure.config;

import com.nihongodev.platform.application.service.generator.PitchAssembler;
import com.nihongodev.platform.application.service.generator.PitchSection;
import com.nihongodev.platform.application.service.generator.sections.*;
import com.nihongodev.platform.domain.model.PitchType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

@Configuration
public class PitchAssemblerConfig {

    @Bean
    public PitchAssembler pitchAssembler() {
        IntroSectionEN introEN = new IntroSectionEN();
        IntroSectionJP introJP = new IntroSectionJP();
        ExperienceSectionEN experienceEN = new ExperienceSectionEN();
        ExperienceSectionJP experienceJP = new ExperienceSectionJP();
        TechStackSection techStack = new TechStackSection();
        MotivationSectionEN motivationEN = new MotivationSectionEN();
        MotivationSectionJP motivationJP = new MotivationSectionJP();
        CertificationsSection certifications = new CertificationsSection();
        ClosingSectionEN closingEN = new ClosingSectionEN();
        ClosingSectionJP closingJP = new ClosingSectionJP();
        InterviewOpeningSection interviewOpening = new InterviewOpeningSection();
        ProjectHighlightsSection projectHighlights = new ProjectHighlightsSection();

        Map<PitchType, List<PitchSection>> registry = Map.of(
                PitchType.ENGLISH_PITCH, List.of(
                        introEN, experienceEN, techStack, motivationEN, certifications, closingEN),
                PitchType.JAPANESE_PITCH, List.of(
                        introJP, experienceJP, techStack, motivationJP, certifications, closingJP),
                PitchType.INTERVIEW_PRESENTATION, List.of(
                        interviewOpening, introEN, experienceEN, motivationEN, projectHighlights, certifications, closingEN),
                PitchType.DEVELOPER_SUMMARY, List.of(
                        introEN, experienceEN, techStack, projectHighlights, certifications)
        );

        return new PitchAssembler(registry);
    }
}
