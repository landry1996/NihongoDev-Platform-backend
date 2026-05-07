package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.command.GeneratePitchCommand;
import com.nihongodev.platform.application.dto.GeneratedPitchDto;
import com.nihongodev.platform.application.port.out.CvProfileRepositoryPort;
import com.nihongodev.platform.application.port.out.EventPublisherPort;
import com.nihongodev.platform.application.port.out.GeneratedPitchRepositoryPort;
import com.nihongodev.platform.application.service.generator.PitchAssembler;
import com.nihongodev.platform.application.service.generator.PitchSection;
import com.nihongodev.platform.domain.event.PitchGeneratedEvent;
import com.nihongodev.platform.domain.exception.ResourceNotFoundException;
import com.nihongodev.platform.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GeneratePitchUseCase")
class GeneratePitchUseCaseTest {

    @Mock private CvProfileRepositoryPort profileRepository;
    @Mock private GeneratedPitchRepositoryPort pitchRepository;
    @Mock private EventPublisherPort eventPublisher;

    private PitchAssembler pitchAssembler;
    private GeneratePitchUseCase useCase;

    @BeforeEach
    void setUp() {
        PitchSection stubSection = new PitchSection() {
            @Override
            public int order() {
                return 1;
            }

            @Override
            public String render(CvProfile profile) {
                return "## Intro\n\nHello";
            }
        };

        pitchAssembler = new PitchAssembler(Map.of(
                PitchType.ENGLISH_PITCH, List.of(stubSection),
                PitchType.JAPANESE_PITCH, List.of(stubSection)
        ));
        useCase = new GeneratePitchUseCase(profileRepository, pitchRepository, eventPublisher, pitchAssembler);
    }

    @Test
    @DisplayName("should generate pitch and save")
    void shouldGeneratePitchAndSave() {
        UUID userId = UUID.randomUUID();
        CvProfile profile = CvProfile.create(userId, "Pierre", "Backend", 5, TargetCompanyType.STARTUP);
        GeneratePitchCommand command = new GeneratePitchCommand(PitchType.ENGLISH_PITCH);

        when(profileRepository.findByUserId(userId)).thenReturn(Optional.of(profile));
        when(pitchRepository.save(any(GeneratedPitch.class))).thenAnswer(inv -> inv.getArgument(0));

        GeneratedPitchDto dto = useCase.generate(userId, command);

        assertThat(dto.pitchType()).isEqualTo(PitchType.ENGLISH_PITCH);
        assertThat(dto.content()).isEqualTo("## Intro\n\nHello");
    }

    @Test
    @DisplayName("should publish PitchGeneratedEvent")
    void shouldPublishEvent() {
        UUID userId = UUID.randomUUID();
        CvProfile profile = CvProfile.create(userId, "Pierre", "Backend", 5, TargetCompanyType.STARTUP);
        GeneratePitchCommand command = new GeneratePitchCommand(PitchType.JAPANESE_PITCH);

        when(profileRepository.findByUserId(userId)).thenReturn(Optional.of(profile));
        when(pitchRepository.save(any(GeneratedPitch.class))).thenAnswer(inv -> inv.getArgument(0));

        useCase.generate(userId, command);

        verify(eventPublisher).publish(eq("cv-generator-events"), any(PitchGeneratedEvent.class));
    }

    @Test
    @DisplayName("should fail if no profile exists")
    void shouldFailIfNoProfile() {
        UUID userId = UUID.randomUUID();
        GeneratePitchCommand command = new GeneratePitchCommand(PitchType.ENGLISH_PITCH);

        when(profileRepository.findByUserId(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.generate(userId, command))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
