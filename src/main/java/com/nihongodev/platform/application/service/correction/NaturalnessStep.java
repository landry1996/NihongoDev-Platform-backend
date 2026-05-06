package com.nihongodev.platform.application.service.correction;

import com.nihongodev.platform.domain.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NaturalnessStep implements CorrectionStep {

    private static final Map<String, String> UNNATURAL_PATTERNS = Map.of(
            "私は私の", "Repetitive 私 — rephrase to avoid redundancy",
            "することができます", "Can simplify to できます",
            "を行うことが", "Can often simplify to する",
            "という風に", "Consider using ように instead (more natural)"
    );

    private static final List<String> NATURAL_CONNECTORS = List.of(
            "また", "さらに", "そのため", "したがって",
            "一方", "しかし", "なお", "つまり"
    );

    @Override
    public CorrectionStepResult process(String text, CorrectionContext context) {
        List<Annotation> annotations = new ArrayList<>();
        double score = 70.0;

        for (Map.Entry<String, String> entry : UNNATURAL_PATTERNS.entrySet()) {
            Pattern pattern = Pattern.compile(Pattern.quote(entry.getKey()));
            Matcher matcher = pattern.matcher(text);
            while (matcher.find()) {
                annotations.add(Annotation.create(
                        matcher.start(), matcher.end(), Severity.INFO,
                        AnnotationCategory.NATURALNESS, entry.getKey(),
                        entry.getValue(), "This expression sounds unnatural to native speakers",
                        "naturalness-" + entry.getKey().hashCode()
                ));
                score -= 5;
            }
        }

        int connectorCount = 0;
        for (String connector : NATURAL_CONNECTORS) {
            if (text.contains(connector)) connectorCount++;
        }
        if (connectorCount > 0 && text.length() > 50) {
            score += Math.min(connectorCount * 5, 20);
        }

        boolean hasJapanese = text.matches(".*[\\u3040-\\u309F\\u30A0-\\u30FF\\u4E00-\\u9FFF].*");
        if (hasJapanese) {
            score += 10;
        }

        score = Math.max(Math.min(score, 100), 0);
        CorrectionScore correctionScore = CorrectionScore.of(0, 0, 0, 0, score, 0);
        return new CorrectionStepResult(correctionScore, annotations);
    }
}
