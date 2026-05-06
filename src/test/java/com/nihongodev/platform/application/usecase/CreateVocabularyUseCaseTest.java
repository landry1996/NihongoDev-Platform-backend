package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.command.CreateVocabularyCommand;
import com.nihongodev.platform.application.dto.VocabularyDto;
import com.nihongodev.platform.application.port.out.VocabularyRepositoryPort;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CreateVocabularyUseCase")
class CreateVocabularyUseCaseTest {

    @Mock private VocabularyRepositoryPort vocabularyRepository;

    private CreateVocabularyUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new CreateVocabularyUseCase(vocabularyRepository);
    }

    @Test
    @DisplayName("should create vocabulary successfully")
    void shouldCreateVocabulary() {
        CreateVocabularyCommand command = new CreateVocabularyCommand(
                "déployer", "deploy", "デプロイ", "depuroi",
                "本番環境にデプロイする", "kubectl apply -f deployment.yaml",
                VocabularyCategory.DEVOPS, VocabularyLevel.B1,
                "CI/CD", List.of("kubernetes", "pipeline"), null
        );

        when(vocabularyRepository.save(any(Vocabulary.class))).thenAnswer(inv -> inv.getArgument(0));

        VocabularyDto result = useCase.create(command);

        assertThat(result).isNotNull();
        assertThat(result.japanese()).isEqualTo("デプロイ");
        assertThat(result.french()).isEqualTo("déployer");
        assertThat(result.category()).isEqualTo(VocabularyCategory.DEVOPS);
        assertThat(result.level()).isEqualTo(VocabularyLevel.B1);
        assertThat(result.tags()).containsExactly("kubernetes", "pipeline");
        assertThat(result.difficultyScore()).isEqualTo(0.5);
        verify(vocabularyRepository).save(any(Vocabulary.class));
    }

    @Test
    @DisplayName("should batch import vocabulary")
    void shouldBatchImport() {
        List<CreateVocabularyCommand> commands = List.of(
                new CreateVocabularyCommand("variable", "variable", "変数", "hensu", null, null,
                        VocabularyCategory.JAVA, VocabularyLevel.BEGINNER, null, List.of("basics"), null),
                new CreateVocabularyCommand("classe", "class", "クラス", "kurasu", null, null,
                        VocabularyCategory.JAVA, VocabularyLevel.BEGINNER, null, List.of("oop"), null)
        );

        when(vocabularyRepository.saveAll(anyList())).thenAnswer(inv -> inv.getArgument(0));

        List<VocabularyDto> results = useCase.batchImport(commands);

        assertThat(results).hasSize(2);
        assertThat(results.get(0).japanese()).isEqualTo("変数");
        assertThat(results.get(1).japanese()).isEqualTo("クラス");
        verify(vocabularyRepository).saveAll(anyList());
    }

    @Test
    @DisplayName("should default level to BEGINNER when null")
    void shouldDefaultLevelBeginner() {
        CreateVocabularyCommand command = new CreateVocabularyCommand(
                "test", "test", "テスト", "tesuto", null, null,
                VocabularyCategory.JAVA, null, null, null, null
        );

        when(vocabularyRepository.save(any(Vocabulary.class))).thenAnswer(inv -> inv.getArgument(0));

        VocabularyDto result = useCase.create(command);

        assertThat(result.level()).isEqualTo(VocabularyLevel.BEGINNER);
    }
}
