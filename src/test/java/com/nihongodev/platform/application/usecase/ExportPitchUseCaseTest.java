package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.port.out.GeneratedPitchRepositoryPort;
import com.nihongodev.platform.domain.exception.UnauthorizedException;
import com.nihongodev.platform.domain.model.ExportFormat;
import com.nihongodev.platform.domain.model.GeneratedPitch;
import com.nihongodev.platform.domain.model.PitchType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ExportPitchUseCase")
class ExportPitchUseCaseTest {

    @Mock private GeneratedPitchRepositoryPort pitchRepository;

    private ExportPitchUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new ExportPitchUseCase(pitchRepository);
    }

    @Test
    @DisplayName("should export as markdown unchanged")
    void shouldExportMarkdown() {
        UUID userId = UUID.randomUUID();
        UUID pitchId = UUID.randomUUID();
        GeneratedPitch pitch = GeneratedPitch.create(userId, PitchType.ENGLISH_PITCH,
                "## Title\n\n**Bold** text\n\n- Item 1", UUID.randomUUID());
        pitch.setId(pitchId);

        when(pitchRepository.findById(pitchId)).thenReturn(Optional.of(pitch));

        String result = useCase.export(userId, pitchId, ExportFormat.MARKDOWN);

        assertThat(result).isEqualTo("## Title\n\n**Bold** text\n\n- Item 1");
    }

    @Test
    @DisplayName("should export as plain text with markdown stripped")
    void shouldExportPlainText() {
        UUID userId = UUID.randomUUID();
        UUID pitchId = UUID.randomUUID();
        GeneratedPitch pitch = GeneratedPitch.create(userId, PitchType.ENGLISH_PITCH,
                "## Title\n\n**Bold** text\n\n- Item 1\n- Item 2", UUID.randomUUID());
        pitch.setId(pitchId);

        when(pitchRepository.findById(pitchId)).thenReturn(Optional.of(pitch));

        String result = useCase.export(userId, pitchId, ExportFormat.PLAIN_TEXT);

        assertThat(result).doesNotContain("##");
        assertThat(result).doesNotContain("**");
        assertThat(result).contains("Title");
        assertThat(result).contains("Bold");
        assertThat(result).contains("Item 1");
    }

    @Test
    @DisplayName("should fail if pitch belongs to another user")
    void shouldFailIfNotOwner() {
        UUID userId = UUID.randomUUID();
        UUID otherUserId = UUID.randomUUID();
        UUID pitchId = UUID.randomUUID();
        GeneratedPitch pitch = GeneratedPitch.create(otherUserId, PitchType.ENGLISH_PITCH, "content", UUID.randomUUID());
        pitch.setId(pitchId);

        when(pitchRepository.findById(pitchId)).thenReturn(Optional.of(pitch));

        assertThatThrownBy(() -> useCase.export(userId, pitchId, ExportFormat.MARKDOWN))
                .isInstanceOf(UnauthorizedException.class);
    }
}
