package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.command.CorrectTextCommand;
import com.nihongodev.platform.application.dto.CorrectionSessionDto;
import com.nihongodev.platform.application.port.out.CorrectionRuleRepositoryPort;
import com.nihongodev.platform.application.port.out.CorrectionSessionRepositoryPort;
import com.nihongodev.platform.application.port.out.EventPublisherPort;
import com.nihongodev.platform.application.port.out.WeaknessPatternRepositoryPort;
import com.nihongodev.platform.domain.model.*;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CorrectTextUseCase")
class CorrectTextUseCaseTest {

    @Mock private CorrectionSessionRepositoryPort sessionRepository;
    @Mock private CorrectionRuleRepositoryPort ruleRepository;
    @Mock private WeaknessPatternRepositoryPort weaknessRepository;
    @Mock private EventPublisherPort eventPublisher;

    private CorrectTextUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new CorrectTextUseCase(sessionRepository, ruleRepository,
                weaknessRepository, eventPublisher);
    }

    @Test
    @DisplayName("should correct text and return session with scores")
    void shouldCorrectText() {
        UUID userId = UUID.randomUUID();
        CorrectTextCommand command = new CorrectTextCommand(
                "お疲れ様です。本日の進捗を報告します。", "STANDUP_REPORT", "N3");

        when(ruleRepository.findAllActive()).thenReturn(List.of());
        when(sessionRepository.save(any(CorrectionSession.class))).thenAnswer(inv -> inv.getArgument(0));
        doNothing().when(eventPublisher).publish(anyString(), any());

        CorrectionSessionDto result = useCase.correct(userId, command);

        assertThat(result).isNotNull();
        assertThat(result.textType()).isEqualTo(TextType.STANDUP_REPORT);
        assertThat(result.score()).isNotNull();
        assertThat(result.score().overallScore()).isGreaterThan(0);
        verify(eventPublisher).publish(eq("correction-events"), any());
    }

    @Test
    @DisplayName("should detect errors in text with grammar issues")
    void shouldDetectGrammarErrors() {
        UUID userId = UUID.randomUUID();
        CorrectTextCommand command = new CorrectTextCommand(
                "このについてを問題のでからです", "FREE_TEXT", "N3");

        when(ruleRepository.findAllActive()).thenReturn(List.of());
        when(sessionRepository.save(any(CorrectionSession.class))).thenAnswer(inv -> inv.getArgument(0));
        doNothing().when(eventPublisher).publish(anyString(), any());

        CorrectionSessionDto result = useCase.correct(userId, command);

        assertThat(result.totalAnnotations()).isGreaterThan(0);
        assertThat(result.errorCount()).isGreaterThan(0);
    }

    @Test
    @DisplayName("should use default values when textType and level are null")
    void shouldUseDefaults() {
        UUID userId = UUID.randomUUID();
        CorrectTextCommand command = new CorrectTextCommand("テスト", null, null);

        when(ruleRepository.findAllActive()).thenReturn(List.of());
        when(sessionRepository.save(any(CorrectionSession.class))).thenAnswer(inv -> inv.getArgument(0));
        doNothing().when(eventPublisher).publish(anyString(), any());

        CorrectionSessionDto result = useCase.correct(userId, command);

        assertThat(result.textType()).isEqualTo(TextType.FREE_TEXT);
        assertThat(result.targetLevel()).isEqualTo(JapaneseLevel.N3);
    }

    @Test
    @DisplayName("should track weakness patterns for errors and warnings")
    void shouldTrackWeaknessPatterns() {
        UUID userId = UUID.randomUUID();
        CorrectTextCommand command = new CorrectTextCommand(
                "このについてを問題です", "FREE_TEXT", "N3");

        when(ruleRepository.findAllActive()).thenReturn(List.of());
        when(sessionRepository.save(any(CorrectionSession.class))).thenAnswer(inv -> inv.getArgument(0));
        when(weaknessRepository.findByUserIdAndCategoryAndDescription(any(), any(), anyString()))
                .thenReturn(Optional.empty());
        when(weaknessRepository.save(any(WeaknessPattern.class))).thenAnswer(inv -> inv.getArgument(0));
        doNothing().when(eventPublisher).publish(anyString(), any());

        useCase.correct(userId, command);

        verify(weaknessRepository, atLeastOnce()).save(any(WeaknessPattern.class));
    }

    @Test
    @DisplayName("should increment existing weakness pattern")
    void shouldIncrementExistingWeakness() {
        UUID userId = UUID.randomUUID();
        CorrectTextCommand command = new CorrectTextCommand(
                "このについてを問題です", "FREE_TEXT", "N3");

        WeaknessPattern existing = WeaknessPattern.create(userId, AnnotationCategory.GRAMMAR,
                "test", "example");

        when(ruleRepository.findAllActive()).thenReturn(List.of());
        when(sessionRepository.save(any(CorrectionSession.class))).thenAnswer(inv -> inv.getArgument(0));
        when(weaknessRepository.findByUserIdAndCategoryAndDescription(any(), any(), anyString()))
                .thenReturn(Optional.of(existing));
        when(weaknessRepository.save(any(WeaknessPattern.class))).thenAnswer(inv -> inv.getArgument(0));
        doNothing().when(eventPublisher).publish(anyString(), any());

        useCase.correct(userId, command);

        assertThat(existing.getOccurrenceCount()).isGreaterThan(1);
    }
}
