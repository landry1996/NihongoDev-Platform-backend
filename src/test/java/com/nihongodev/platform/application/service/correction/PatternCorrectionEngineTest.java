package com.nihongodev.platform.application.service.correction;

import com.nihongodev.platform.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("PatternCorrectionEngine")
class PatternCorrectionEngineTest {

    private PatternCorrectionEngine engine;
    private CorrectionContext context;

    @BeforeEach
    void setUp() {
        engine = new PatternCorrectionEngine();
        context = CorrectionContext.of(TextType.FREE_TEXT, JapaneseLevel.N3);
    }

    @Test
    @DisplayName("should apply matching rules and produce annotations")
    void shouldApplyMatchingRules() {
        CorrectionRule rule = CorrectionRule.create("test", AnnotationCategory.GRAMMAR,
                Severity.WARNING, "テスト", "テストの修正", "explanation", List.of(), JapaneseLevel.N5);

        List<Annotation> results = engine.applyRules("これはテストです", List.of(rule), context);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getStartOffset()).isEqualTo(3);
        assertThat(results.get(0).getEndOffset()).isEqualTo(6);
    }

    @Test
    @DisplayName("should skip rules that do not match context")
    void shouldSkipNonMatchingContext() {
        CorrectionRule rule = CorrectionRule.create("email-only", AnnotationCategory.KEIGO,
                Severity.WARNING, "だ[。]", "です。", "Use です",
                List.of(TextType.EMAIL_TO_CLIENT), JapaneseLevel.N5);

        CorrectionContext slackContext = CorrectionContext.of(TextType.SLACK_MESSAGE, JapaneseLevel.N3);
        List<Annotation> results = engine.applyRules("これだ。", List.of(rule), slackContext);

        assertThat(results).isEmpty();
    }

    @Test
    @DisplayName("should handle invalid regex patterns gracefully")
    void shouldHandleInvalidRegex() {
        CorrectionRule badRule = CorrectionRule.create("bad", AnnotationCategory.GRAMMAR,
                Severity.ERROR, "[invalid(regex", "suggestion", "explanation", List.of(), JapaneseLevel.N5);

        List<Annotation> results = engine.applyRules("some text", List.of(badRule), context);

        assertThat(results).isEmpty();
    }

    @Test
    @DisplayName("should find multiple matches for same rule")
    void shouldFindMultipleMatches() {
        CorrectionRule rule = CorrectionRule.create("test", AnnotationCategory.VOCABULARY,
                Severity.INFO, "する", "implement better verb", "explanation", List.of(), JapaneseLevel.N5);

        List<Annotation> results = engine.applyRules("AをするBをするCをする", List.of(rule), context);

        assertThat(results).hasSizeGreaterThanOrEqualTo(3);
    }
}
