package com.nihongodev.platform.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("CorrectionSession Domain Model")
class CorrectionSessionTest {

    @Test
    @DisplayName("should create session with correct initial state")
    void shouldCreateWithCorrectState() {
        CorrectionSession session = CorrectionSession.create(
                UUID.randomUUID(), "テスト", TextType.STANDUP_REPORT, JapaneseLevel.N3);

        assertThat(session.getId()).isNotNull();
        assertThat(session.getTextType()).isEqualTo(TextType.STANDUP_REPORT);
        assertThat(session.getTargetLevel()).isEqualTo(JapaneseLevel.N3);
        assertThat(session.getScore()).isNotNull();
        assertThat(session.getAnnotations()).isEmpty();
        assertThat(session.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("should apply results and count severities")
    void shouldApplyResults() {
        CorrectionSession session = CorrectionSession.create(
                UUID.randomUUID(), "text", TextType.FREE_TEXT, JapaneseLevel.N3);

        List<Annotation> annotations = List.of(
                Annotation.create(0, 5, Severity.ERROR, AnnotationCategory.GRAMMAR, "a", "b", "c", "r1"),
                Annotation.create(6, 10, Severity.WARNING, AnnotationCategory.KEIGO, "d", "e", "f", "r2"),
                Annotation.create(11, 15, Severity.INFO, AnnotationCategory.CLARITY, "g", "h", "i", "r3"),
                Annotation.create(16, 20, Severity.ERROR, AnnotationCategory.GRAMMAR, "j", "k", "l", "r4")
        );

        CorrectionScore score = CorrectionScore.of(80, 70, 60, 75, 65, 85);
        session.applyResults(score, annotations);

        assertThat(session.getTotalAnnotations()).isEqualTo(4);
        assertThat(session.getErrorCount()).isEqualTo(2);
        assertThat(session.getWarningCount()).isEqualTo(1);
        assertThat(session.getInfoCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("should detect presence of errors")
    void shouldDetectErrors() {
        CorrectionSession session = CorrectionSession.create(
                UUID.randomUUID(), "text", TextType.FREE_TEXT, JapaneseLevel.N3);

        session.applyResults(CorrectionScore.zero(), List.of(
                Annotation.create(0, 5, Severity.ERROR, AnnotationCategory.GRAMMAR, "a", "b", "c", "r1")
        ));

        assertThat(session.hasErrors()).isTrue();
        assertThat(session.hasWarnings()).isFalse();
    }

    @Test
    @DisplayName("should detect no errors when only info present")
    void shouldDetectNoErrors() {
        CorrectionSession session = CorrectionSession.create(
                UUID.randomUUID(), "text", TextType.FREE_TEXT, JapaneseLevel.N3);

        session.applyResults(CorrectionScore.zero(), List.of(
                Annotation.create(0, 5, Severity.INFO, AnnotationCategory.CLARITY, "a", "b", "c", "r1")
        ));

        assertThat(session.hasErrors()).isFalse();
        assertThat(session.hasWarnings()).isFalse();
    }
}
