package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.port.out.VocabularyRepositoryPort;
import com.nihongodev.platform.domain.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DeleteVocabularyUseCase")
class DeleteVocabularyUseCaseTest {

    @Mock private VocabularyRepositoryPort vocabularyRepository;

    private DeleteVocabularyUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new DeleteVocabularyUseCase(vocabularyRepository);
    }

    @Test
    @DisplayName("should delete vocabulary successfully")
    void shouldDelete() {
        UUID id = UUID.randomUUID();
        when(vocabularyRepository.existsById(id)).thenReturn(true);

        useCase.delete(id);

        verify(vocabularyRepository).deleteById(id);
    }

    @Test
    @DisplayName("should throw when vocabulary not found")
    void shouldThrowWhenNotFound() {
        UUID id = UUID.randomUUID();
        when(vocabularyRepository.existsById(id)).thenReturn(false);

        assertThatThrownBy(() -> useCase.delete(id))
                .isInstanceOf(ResourceNotFoundException.class);
        verify(vocabularyRepository, never()).deleteById(any());
    }
}
