package com.nihongodev.platform.application.service.correction;

import com.nihongodev.platform.domain.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GrammarStep implements CorrectionStep {

    private static final List<GrammarRule> RULES = List.of(
            new GrammarRule("を([あ-ん])る", "を$1る → correct particle usage check",
                    "Verify particle を usage with this verb", Severity.WARNING),
            new GrammarRule("([^で])しょう$", "$1でしょう",
                    "でしょう requires で before しょう in formal contexts", Severity.INFO),
            new GrammarRule("だと思います", "と思います",
                    "だ is redundant before と思います in formal writing", Severity.WARNING),
            new GrammarRule("する事", "すること",
                    "Use hiragana こと instead of kanji 事 for nominalizer", Severity.INFO),
            new GrammarRule("についてを", "について",
                    "について already contains the particle function, を is redundant", Severity.ERROR),
            new GrammarRule("のでから", "ので or から",
                    "Cannot combine ので and から — choose one causal connector", Severity.ERROR)
    );

    @Override
    public CorrectionStepResult process(String text, CorrectionContext context) {
        List<Annotation> annotations = new ArrayList<>();
        double score = 100.0;

        for (GrammarRule rule : RULES) {
            Pattern pattern = Pattern.compile(rule.pattern());
            Matcher matcher = pattern.matcher(text);
            while (matcher.find()) {
                annotations.add(Annotation.create(
                        matcher.start(), matcher.end(), rule.severity(),
                        AnnotationCategory.GRAMMAR, matcher.group(),
                        rule.suggestion(), rule.explanation(), "grammar-" + rule.pattern()
                ));
                score -= penaltyFor(rule.severity());
            }
        }

        if (text.length() > 20 && !text.contains("。") && !text.contains("、")) {
            score -= 5;
        }

        score = Math.max(score, 0);

        CorrectionScore correctionScore = CorrectionScore.of(score, 0, 0, 0, 0, 0);
        return new CorrectionStepResult(correctionScore, annotations);
    }

    private double penaltyFor(Severity severity) {
        return switch (severity) {
            case ERROR, CRITICAL -> 15;
            case WARNING, MODERATE -> 8;
            case INFO, MINOR -> 3;
        };
    }

    private record GrammarRule(String pattern, String suggestion, String explanation, Severity severity) {}
}
