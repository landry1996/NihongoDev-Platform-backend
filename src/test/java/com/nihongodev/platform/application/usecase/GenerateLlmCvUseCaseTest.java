package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.dto.GeneratedPitchDto;
import com.nihongodev.platform.application.port.out.CvProfileRepositoryPort;
import com.nihongodev.platform.application.port.out.EventPublisherPort;
import com.nihongodev.platform.application.port.out.GeneratedPitchRepositoryPort;
import com.nihongodev.platform.application.port.out.LlmPort;
import com.nihongodev.platform.domain.exception.ResourceNotFoundException;
import com.nihongodev.platform.domain.model.CvProfile;
import com.nihongodev.platform.domain.model.GeneratedPitch;
import com.nihongodev.platform.domain.model.PitchType;
import com.nihongodev.platform.domain.model.TargetCompanyType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GenerateLlmCvUseCaseTest {

    @Mock
    private CvProfileRepositoryPort cvProfileRepository;

    @Mock
    private GeneratedPitchRepositoryPort pitchRepository;

    @Mock
    private LlmPort llmPort;

    @Mock
    private EventPublisherPort eventPublisher;

    private GenerateLlmCvUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new GenerateLlmCvUseCase(cvProfileRepository, pitchRepository, llmPort, eventPublisher);
    }

    @Test
    void shouldGenerateLlmCvSuccessfully() {
        UUID userId = UUID.randomUUID();
        CvProfile profile = CvProfile.create(userId, "Taro Yamada", "Senior Engineer", 5, TargetCompanyType.STARTUP);
        profile.setTechStack(List.of("Java", "Spring Boot", "Kafka"));
        profile.setJapaneseLevel("N2");

        when(cvProfileRepository.findByUserId(userId)).thenReturn(Optional.of(profile));
        when(llmPort.generate(anyString(), anyString())).thenReturn("# Taro Yamada\n\nGenerated pitch content...");
        when(pitchRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        GeneratedPitchDto result = useCase.generate(userId, "ENGLISH_PITCH", "STARTUP", null);

        assertThat(result).isNotNull();
        assertThat(result.pitchType()).isEqualTo(PitchType.ENGLISH_PITCH);
        assertThat(result.content()).contains("Taro Yamada");
        verify(llmPort).generate(anyString(), anyString());
        verify(eventPublisher).publish(eq("cv-generator-events"), any());
    }

    @Test
    void shouldThrowWhenProfileNotFound() {
        UUID userId = UUID.randomUUID();
        when(cvProfileRepository.findByUserId(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.generate(userId, "ENGLISH_PITCH", null, null))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void shouldUseProfileTargetCompanyWhenNotSpecified() {
        UUID userId = UUID.randomUUID();
        CvProfile profile = CvProfile.create(userId, "Hanako", "DevOps", 3, TargetCompanyType.TRADITIONAL_JAPANESE);

        when(cvProfileRepository.findByUserId(userId)).thenReturn(Optional.of(profile));
        when(llmPort.generate(anyString(), anyString())).thenReturn("Generated content");
        when(pitchRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        GeneratedPitchDto result = useCase.generate(userId, "JAPANESE_PITCH", null, "Focus on teamwork");

        assertThat(result).isNotNull();
        verify(llmPort).generate(contains("very formal keigo"), contains("Focus on teamwork"));
    }
}
