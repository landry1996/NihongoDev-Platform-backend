package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.command.SearchVocabularyCommand;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("SearchVocabularyUseCase")
class SearchVocabularyUseCaseTest {

    @Mock private VocabularyRepositoryPort vocabularyRepository;

    private SearchVocabularyUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new SearchVocabularyUseCase(vocabularyRepository);
    }

    @Test
    @DisplayName("should get by ID")
    void shouldGetById() {
        UUID id = UUID.randomUUID();
        Vocabulary v = Vocabulary.create("test", "test", "テスト", "tesuto",
                null, null, VocabularyCategory.JAVA, VocabularyLevel.BEGINNER, null, null, null);
        v.setId(id);

        when(vocabularyRepository.findById(id)).thenReturn(Optional.of(v));

        VocabularyDto result = useCase.getById(id);
        assertThat(result.id()).isEqualTo(id);
        assertThat(result.japanese()).isEqualTo("テスト");
    }

    @Test
    @DisplayName("should throw when not found by ID")
    void shouldThrowWhenNotFound() {
        UUID id = UUID.randomUUID();
        when(vocabularyRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.getById(id))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("should search with filters")
    void shouldSearchWithFilters() {
        Vocabulary v1 = Vocabulary.create("déployer", "deploy", "デプロイ", "depuroi",
                null, null, VocabularyCategory.DEVOPS, VocabularyLevel.B1, null, null, null);

        SearchVocabularyCommand command = new SearchVocabularyCommand(
                "deploy", VocabularyCategory.DEVOPS, null, null, null
        );

        when(vocabularyRepository.search("deploy", VocabularyCategory.DEVOPS, null, null, null))
                .thenReturn(List.of(v1));

        List<VocabularyDto> results = useCase.search(command);

        assertThat(results).hasSize(1);
        assertThat(results.getFirst().category()).isEqualTo(VocabularyCategory.DEVOPS);
    }

    @Test
    @DisplayName("should get by lesson ID")
    void shouldGetByLessonId() {
        UUID lessonId = UUID.randomUUID();
        Vocabulary v1 = Vocabulary.create("test", "test", "テスト", "tesuto",
                null, null, VocabularyCategory.JAVA, VocabularyLevel.BEGINNER, null, null, lessonId);

        when(vocabularyRepository.findByLessonId(lessonId)).thenReturn(List.of(v1));

        List<VocabularyDto> results = useCase.getByLessonId(lessonId);
        assertThat(results).hasSize(1);
        assertThat(results.getFirst().lessonId()).isEqualTo(lessonId);
    }
}
