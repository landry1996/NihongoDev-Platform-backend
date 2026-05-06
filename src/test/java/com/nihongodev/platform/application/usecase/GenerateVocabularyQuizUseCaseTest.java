package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.command.GenerateVocabularyQuizCommand;
import com.nihongodev.platform.application.dto.VocabularyQuizDto;
import com.nihongodev.platform.application.port.out.EventPublisherPort;
import com.nihongodev.platform.application.port.out.VocabularyRepositoryPort;
import com.nihongodev.platform.domain.exception.BusinessException;
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
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("GenerateVocabularyQuizUseCase")
class GenerateVocabularyQuizUseCaseTest {

    @Mock private VocabularyRepositoryPort vocabularyRepository;
    @Mock private EventPublisherPort eventPublisher;

    private GenerateVocabularyQuizUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new GenerateVocabularyQuizUseCase(vocabularyRepository, eventPublisher);
    }

    @Test
    @DisplayName("should generate quiz with enough vocabulary")
    void shouldGenerateQuiz() {
        List<Vocabulary> pool = List.of(
                createVocab("変数", "variable", "hensu"),
                createVocab("クラス", "classe", "kurasu"),
                createVocab("メソッド", "méthode", "mesoddo"),
                createVocab("配列", "tableau", "hairetsu"),
                createVocab("インターフェース", "interface", "intaafeesu")
        );

        GenerateVocabularyQuizCommand command = new GenerateVocabularyQuizCommand(
                "FR_TO_JP", VocabularyCategory.JAVA, VocabularyLevel.BEGINNER, 4
        );

        when(vocabularyRepository.findByCategoryAndLevel(VocabularyCategory.JAVA, VocabularyLevel.BEGINNER))
                .thenReturn(pool);

        VocabularyQuizDto result = useCase.generate(UUID.randomUUID(), command);

        assertThat(result).isNotNull();
        assertThat(result.quizType()).isEqualTo("FR_TO_JP");
        assertThat(result.wordCount()).isEqualTo(4);
        assertThat(result.items()).hasSize(4);
        result.items().forEach(item -> {
            assertThat(item.options()).hasSizeGreaterThanOrEqualTo(2);
            assertThat(item.correctAnswer()).isNotBlank();
        });
        verify(eventPublisher).publish(eq("vocabulary-events"), any());
    }

    @Test
    @DisplayName("should throw when pool too small")
    void shouldThrowWhenPoolTooSmall() {
        List<Vocabulary> pool = List.of(
                createVocab("テスト", "test", "tesuto"),
                createVocab("バグ", "bug", "bagu")
        );

        GenerateVocabularyQuizCommand command = new GenerateVocabularyQuizCommand(
                "FR_TO_JP", VocabularyCategory.JAVA, null, 5
        );

        when(vocabularyRepository.findByCategory(VocabularyCategory.JAVA)).thenReturn(pool);

        assertThatThrownBy(() -> useCase.generate(UUID.randomUUID(), command))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Not enough vocabulary");
    }

    @Test
    @DisplayName("should generate JP_TO_FR quiz type")
    void shouldGenerateJpToFrQuiz() {
        List<Vocabulary> pool = List.of(
                createVocab("変数", "variable", "hensu"),
                createVocab("クラス", "classe", "kurasu"),
                createVocab("メソッド", "méthode", "mesoddo"),
                createVocab("配列", "tableau", "hairetsu")
        );

        GenerateVocabularyQuizCommand command = new GenerateVocabularyQuizCommand(
                "JP_TO_FR", null, null, 4
        );

        when(vocabularyRepository.findAll()).thenReturn(pool);

        VocabularyQuizDto result = useCase.generate(UUID.randomUUID(), command);

        assertThat(result.quizType()).isEqualTo("JP_TO_FR");
        assertThat(result.items()).hasSize(4);
    }

    private Vocabulary createVocab(String japanese, String french, String romaji) {
        Vocabulary v = Vocabulary.create(french, french, japanese, romaji, null, null,
                VocabularyCategory.JAVA, VocabularyLevel.BEGINNER, null, null, null);
        v.setId(UUID.randomUUID());
        return v;
    }
}
