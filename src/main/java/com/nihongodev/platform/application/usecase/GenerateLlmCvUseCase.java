package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.dto.GeneratedPitchDto;
import com.nihongodev.platform.application.port.in.GenerateLlmCvPort;
import com.nihongodev.platform.application.port.out.CvProfileRepositoryPort;
import com.nihongodev.platform.application.port.out.EventPublisherPort;
import com.nihongodev.platform.application.port.out.GeneratedPitchRepositoryPort;
import com.nihongodev.platform.application.port.out.LlmPort;
import com.nihongodev.platform.domain.event.PitchGeneratedEvent;
import com.nihongodev.platform.domain.exception.ResourceNotFoundException;
import com.nihongodev.platform.domain.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class GenerateLlmCvUseCase implements GenerateLlmCvPort {

    private static final Logger log = LoggerFactory.getLogger(GenerateLlmCvUseCase.class);

    private final CvProfileRepositoryPort cvProfileRepository;
    private final GeneratedPitchRepositoryPort pitchRepository;
    private final LlmPort llmPort;
    private final EventPublisherPort eventPublisher;

    public GenerateLlmCvUseCase(CvProfileRepositoryPort cvProfileRepository,
                                GeneratedPitchRepositoryPort pitchRepository,
                                LlmPort llmPort,
                                EventPublisherPort eventPublisher) {
        this.cvProfileRepository = cvProfileRepository;
        this.pitchRepository = pitchRepository;
        this.llmPort = llmPort;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public GeneratedPitchDto generate(UUID userId, String pitchType, String targetCompanyType,
                                      String additionalInstructions) {
        CvProfile profile = cvProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("CvProfile", "userId", userId));

        PitchType type = PitchType.valueOf(pitchType);
        TargetCompanyType companyType = targetCompanyType != null
                ? TargetCompanyType.valueOf(targetCompanyType)
                : profile.getTargetCompanyType();

        String systemPrompt = buildSystemPrompt(type, companyType);
        String userPrompt = buildUserPrompt(profile, additionalInstructions);

        log.info("Generating LLM CV [userId={}, pitchType={}, companyType={}]", userId, type, companyType);
        String content = llmPort.generate(systemPrompt, userPrompt);

        GeneratedPitch pitch = GeneratedPitch.create(userId, type, content, profile.getId());
        pitchRepository.save(pitch);

        eventPublisher.publish("cv-generator-events", PitchGeneratedEvent.of(userId, pitch.getId(), type.name()));

        return new GeneratedPitchDto(pitch.getId(), pitch.getPitchType(), pitch.getContent(), pitch.getGeneratedAt());
    }

    private String buildSystemPrompt(PitchType type, TargetCompanyType companyType) {
        String language = switch (type) {
            case JAPANESE_PITCH, INTERVIEW_PRESENTATION -> "Japanese (using appropriate keigo)";
            case ENGLISH_PITCH, DEVELOPER_SUMMARY -> "English";
        };

        String tone = switch (companyType) {
            case STARTUP -> "modern, casual-professional, emphasize innovation and agility";
            case ENTERPRISE -> "formal, structured, emphasize scalability and enterprise patterns";
            case FOREIGN_IN_JAPAN -> "bilingual-aware, bridge cultural contexts, show adaptability";
            case TRADITIONAL_JAPANESE -> "very formal keigo, humble tone, emphasize team harmony and dedication";
        };

        return """
                You are a professional CV and self-introduction writer specialized in IT professionals \
                targeting the Japanese job market. Generate a polished %s in %s.

                Tone: %s

                Format the output in clean Markdown. Include:
                - A compelling opening statement
                - Technical expertise summary
                - Key achievements with metrics where possible
                - Motivation for working in Japan / with Japanese companies
                - Japanese language ability context

                Keep it concise (300-500 words). Be authentic, not generic.""".formatted(
                type.name().toLowerCase().replace('_', ' '), language, tone);
    }

    private String buildUserPrompt(CvProfile profile, String additionalInstructions) {
        StringBuilder sb = new StringBuilder();
        sb.append("Generate a professional pitch based on this profile:\n\n");
        sb.append("Name: ").append(profile.getFullName()).append("\n");

        if (profile.getCurrentRole() != null) {
            sb.append("Current Role: ").append(profile.getCurrentRole()).append("\n");
        }
        sb.append("Target Role: ").append(profile.getTargetRole()).append("\n");
        sb.append("Years of Experience: ").append(profile.getYearsOfExperience()).append("\n");

        if (profile.getTechStack() != null && !profile.getTechStack().isEmpty()) {
            sb.append("Tech Stack: ").append(String.join(", ", profile.getTechStack())).append("\n");
        }

        if (profile.getExperiences() != null && !profile.getExperiences().isEmpty()) {
            sb.append("\nWork Experience:\n");
            profile.getExperiences().forEach(exp ->
                    sb.append("- ").append(exp.getRole()).append(" at ").append(exp.getCompany())
                            .append(" (").append(exp.getDurationMonths()).append(" months)")
                            .append(exp.getHighlights() != null ? " — " + String.join(", ", exp.getHighlights()) : "")
                            .append("\n"));
        }

        if (profile.getCertifications() != null && !profile.getCertifications().isEmpty()) {
            sb.append("\nCertifications: ").append(String.join(", ", profile.getCertifications())).append("\n");
        }

        if (profile.getNotableProjects() != null && !profile.getNotableProjects().isEmpty()) {
            sb.append("\nNotable Projects:\n");
            profile.getNotableProjects().forEach(p -> sb.append("- ").append(p).append("\n"));
        }

        if (profile.getMotivationJapan() != null) {
            sb.append("\nMotivation for Japan: ").append(profile.getMotivationJapan()).append("\n");
        }

        if (profile.getJapaneseLevel() != null) {
            sb.append("Japanese Level: ").append(profile.getJapaneseLevel()).append("\n");
        }

        if (additionalInstructions != null && !additionalInstructions.isBlank()) {
            sb.append("\nAdditional instructions: ").append(additionalInstructions).append("\n");
        }

        return sb.toString();
    }
}
