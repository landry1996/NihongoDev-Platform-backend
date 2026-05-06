package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.dto.VocabularyMasteryDto;
import com.nihongodev.platform.application.port.out.VocabularyMasteryRepositoryPort;
import com.nihongodev.platform.application.port.out.VocabularyRepositoryPort;
import com.nihongodev.platform.domain.exception.ResourceNotFoundException;
import com.nihongodev.platform.domain.model.MasteryLevel;
import com.nihongodev.platform.domain.model.VocabularyMastery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReviewVocabularyUseCase")
class ReviewVocabularyUseCaseTest {

    @Mock private VocabularyMasteryRepositoryPort masteryRepository;
    @Mock private VocabularyRepositoryPort vocabularyRepository;

    private ReviewVocabularyUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new ReviewVocabularyUseCase(masteryRepository, vocabularyRepository);
    }

    @Test
    @DisplayName("should create mastery on first review")
    void shouldCreateMasteryOnFirstReview() {
        UUID userId = UUID.randomUUID();
        UUID vocabId = UUID.randomUUID();

        when(vocabularyRepository.existsById(vocabId)).thenReturn(true);
        when(masteryRepository.findByUserIdAndVocabularyId(userId, vocabId)).thenReturn(Optional.empty());
        when(masteryRepository.save(any(VocabularyMastery.class))).thenAnswer(inv -> inv.getArgument(0));

        VocabularyMasteryDto result = useCase.recordReview(userId, vocabId, true);

        assertThat(result).isNotNull();
        assertThat(result.masteryLevel()).isEqualTo(MasteryLevel.LEARNING);
        assertThat(result.repetitions()).isEqualTo(1);
        assertThat(result.correctCount()).isEqualTo(1);
        verify(masteryRepository).save(any());
    }

    @Test
    @DisplayName("should update existing mastery on incorrect review")
    void shouldUpdateOnIncorrectReview() {
        UUID userId = UUID.randomUUID();
        UUID vocabId = UUID.randomUUID();
        VocabularyMastery existing = VocabularyMastery.create(userId, vocabId);
        existing.recordReview(true);
        existing.recordReview(true);

        when(vocabularyRepository.existsById(vocabId)).thenReturn(true);
        when(masteryRepository.findByUserIdAndVocabularyId(userId, vocabId)).thenReturn(Optional.of(existing));
        when(masteryRepository.save(any(VocabularyMastery.class))).thenAnswer(inv -> inv.getArgument(0));

        VocabularyMasteryDto result = useCase.recordReview(userId, vocabId, false);

        assertThat(result.repetitions()).isEqualTo(0);
        assertThat(result.incorrectCount()).isEqualTo(1);
        assertThat(result.masteryLevel()).isEqualTo(MasteryLevel.NEW);
    }

    @Test
    @DisplayName("should throw when vocabulary not found")
    void shouldThrowWhenVocabularyNotFound() {
        UUID userId = UUID.randomUUID();
        UUID vocabId = UUID.randomUUID();

        when(vocabularyRepository.existsById(vocabId)).thenReturn(false);

        assertThatThrownBy(() -> useCase.recordReview(userId, vocabId, true))
                .isInstanceOf(ResourceNotFoundException.class);
        verify(masteryRepository, never()).save(any());
    }

    @Test
    @DisplayName("should get due for review")
    void shouldGetDueForReview() {
        UUID userId = UUID.randomUUID();
        VocabularyMastery m = VocabularyMastery.create(userId, UUID.randomUUID());

        when(masteryRepository.findDueForReview(eq(userId), any(LocalDateTime.class))).thenReturn(List.of(m));

        List<VocabularyMasteryDto> results = useCase.getDueForReview(userId);
        assertThat(results).hasSize(1);
    }
}
