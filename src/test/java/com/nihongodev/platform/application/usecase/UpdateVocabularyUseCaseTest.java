package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.command.UpdateVocabularyCommand;
import com.nihongodev.platform.application.dto.VocabularyDto;
import com.nihongodev.platform.application.port.out.VocabularyRepositoryPort;
import com.nihongodev.platform.domain.exception.ResourceNotFoundException;
import com.nihongodev.platform.domain.model.Vocabulary;
import com.nihongodev.platform.domain.model.VocabularyCategory;
import com.nihongodev.platform.domain.model.VocabularyLevel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UpdateVocabularyUseCase")
class UpdateVocabularyUseCaseTest {

    @Mock private VocabularyRepositoryPort vocabularyRepository;

    private UpdateVocabularyUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new UpdateVocabularyUseCase(vocabularyRepository);
    }

    @Test
    @DisplayName("should update vocabulary successfully")
    void shouldUpdateVocabulary() {
        UUID id = UUID.randomUUID();
        Vocabulary existing = Vocabulary.create("ancien", "old", "古い", "furui", null, null,
                VocabularyCategory.JAVA, VocabularyLevel.A1, null, null, null);
        existing.setId(id);

        UpdateVocabularyCommand command = new UpdateVocabularyCommand(
                id, "nouveau", "new", "新しい", "atarashii", null, null,
                VocabularyCategory.SPRING, VocabularyLevel.A2, null, List.of("adj"), null
        );

        when(vocabularyRepository.findById(id)).thenReturn(Optional.of(existing));
        when(vocabularyRepository.save(any(Vocabulary.class))).thenAnswer(inv -> inv.getArgument(0));

        VocabularyDto result = useCase.update(command);

        assertThat(result.french()).isEqualTo("nouveau");
        assertThat(result.japanese()).isEqualTo("新しい");
        assertThat(result.category()).isEqualTo(VocabularyCategory.SPRING);
        verify(vocabularyRepository).save(any());
    }

    @Test
    @DisplayName("should throw when vocabulary not found")
    void shouldThrowWhenNotFound() {
        UUID id = UUID.randomUUID();
        UpdateVocabularyCommand command = new UpdateVocabularyCommand(
                id, "test", null, null, null, null, null, null, null, null, null, null
        );

        when(vocabularyRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.update(command))
                .isInstanceOf(ResourceNotFoundException.class);
        verify(vocabularyRepository, never()).save(any());
    }

    @Test
    @DisplayName("should partially update (only french)")
    void shouldPartiallyUpdate() {
        UUID id = UUID.randomUUID();
        Vocabulary existing = Vocabulary.create("original", "original", "オリジナル", "orijinaru",
                null, null, VocabularyCategory.JAVA, VocabularyLevel.BEGINNER, null, null, null);
        existing.setId(id);

        UpdateVocabularyCommand command = new UpdateVocabularyCommand(
                id, "modifié", null, null, null, null, null, null, null, null, null, null
        );

        when(vocabularyRepository.findById(id)).thenReturn(Optional.of(existing));
        when(vocabularyRepository.save(any(Vocabulary.class))).thenAnswer(inv -> inv.getArgument(0));

        VocabularyDto result = useCase.update(command);

        assertThat(result.french()).isEqualTo("modifié");
        assertThat(result.english()).isEqualTo("original");
        assertThat(result.category()).isEqualTo(VocabularyCategory.JAVA);
    }
}
