package com.nihongodev.platform.application.service.correction;

import com.nihongodev.platform.domain.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PolitenessStep implements CorrectionStep {

    private static final List<String> KEIGO_MARKERS = List.of(
            "ございます", "いただ", "させていただ", "申し上げ",
            "存じ", "拝見", "拝読", "お忙しいところ"
    );

    private static final List<String> DESU_MASU = List.of(
            "です", "ます", "ました", "でした", "ません"
    );

    private static final List<String> CASUAL_ENDINGS = List.of(
            "だよ", "だね", "じゃん", "っす", "だろ", "かな"
    );

    @Override
    public CorrectionStepResult process(String text, CorrectionContext context) {
        List<Annotation> annotations = new ArrayList<>();
        double score = 60.0;

        boolean strictKeigo = context.isStrictKeigo();

        int keigoCount = 0;
        for (String marker : KEIGO_MARKERS) {
            if (text.contains(marker)) keigoCount++;
        }

        int desuMasuCount = 0;
        for (String marker : DESU_MASU) {
            if (text.contains(marker)) desuMasuCount++;
        }

        for (String casual : CASUAL_ENDINGS) {
            Pattern pattern = Pattern.compile(Pattern.quote(casual));
            Matcher matcher = pattern.matcher(text);
            while (matcher.find()) {
                Severity severity = strictKeigo ? Severity.ERROR : Severity.INFO;
                annotations.add(Annotation.create(
                        matcher.start(), matcher.end(), severity,
                        AnnotationCategory.KEIGO, casual,
                        "Use formal ending (です/ます form)",
                        "Casual language detected in a context requiring formal speech",
                        "keigo-casual-" + casual
                ));
                score -= strictKeigo ? 15 : 5;
            }
        }

        if (strictKeigo) {
            if (keigoCount == 0 && text.length() > 30) {
                annotations.add(Annotation.create(
                        0, Math.min(text.length(), 10), Severity.WARNING,
                        AnnotationCategory.KEIGO, "",
                        "Add keigo expressions (いただく, ございます, etc.)",
                        "This context requires honorific language (keigo)",
                        "keigo-missing"
                ));
                score -= 10;
            } else {
                score += keigoCount * 8;
            }
        }

        score += desuMasuCount * 5;
        score = Math.max(Math.min(score, 100), 0);

        CorrectionScore correctionScore = CorrectionScore.of(0, 0, score, 0, 0, 0);
        return new CorrectionStepResult(correctionScore, annotations);
    }
}
