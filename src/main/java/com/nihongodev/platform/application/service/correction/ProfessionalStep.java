package com.nihongodev.platform.application.service.correction;

import com.nihongodev.platform.domain.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProfessionalStep implements CorrectionStep {

    private static final List<String> PROFESSIONAL_OPENERS = List.of(
            "お疲れ様です", "お世話になっております", "ご連絡いただき",
            "ご確認", "ご報告"
    );

    private static final List<String> PROFESSIONAL_CLOSERS = List.of(
            "よろしくお願いいたします", "よろしくお願いします",
            "ご確認ください", "以上です", "ご検討"
    );

    private static final List<String> IT_PROFESSIONAL_TERMS = List.of(
            "実装", "設計", "テスト", "デプロイ", "リリース",
            "レビュー", "修正", "対応", "確認", "報告",
            "進捗", "課題", "優先度", "期限", "完了"
    );

    @Override
    public CorrectionStepResult process(String text, CorrectionContext context) {
        List<Annotation> annotations = new ArrayList<>();
        double score = 60.0;

        TextType textType = context.getTextType();

        if (textType == TextType.EMAIL_TO_CLIENT || textType == TextType.STANDUP_REPORT) {
            boolean hasOpener = PROFESSIONAL_OPENERS.stream().anyMatch(text::contains);
            boolean hasCloser = PROFESSIONAL_CLOSERS.stream().anyMatch(text::contains);

            if (!hasOpener && text.length() > 30) {
                annotations.add(Annotation.create(
                        0, Math.min(text.length(), 5), Severity.WARNING,
                        AnnotationCategory.PROFESSIONAL, "",
                        "Start with a greeting (お疲れ様です, お世話になっております)",
                        "Professional communications should begin with an appropriate greeting",
                        "pro-missing-opener"
                ));
                score -= 10;
            } else if (hasOpener) {
                score += 10;
            }

            if (!hasCloser && text.length() > 30) {
                annotations.add(Annotation.create(
                        Math.max(0, text.length() - 5), text.length(), Severity.INFO,
                        AnnotationCategory.PROFESSIONAL, "",
                        "End with よろしくお願いいたします or similar closing",
                        "Professional communications should end with an appropriate closing",
                        "pro-missing-closer"
                ));
                score -= 5;
            } else if (hasCloser) {
                score += 10;
            }
        }

        int itTermCount = 0;
        for (String term : IT_PROFESSIONAL_TERMS) {
            if (text.contains(term)) itTermCount++;
        }
        score += Math.min(itTermCount * 3, 20);

        Pattern emojiPattern = Pattern.compile("[\\x{1F600}-\\x{1F64F}\\x{1F300}-\\x{1F5FF}]");
        Matcher emojiMatcher = emojiPattern.matcher(text);
        if (emojiMatcher.find() && textType == TextType.EMAIL_TO_CLIENT) {
            annotations.add(Annotation.create(
                    emojiMatcher.start(), emojiMatcher.end(), Severity.WARNING,
                    AnnotationCategory.PROFESSIONAL, emojiMatcher.group(),
                    "Remove emoji in formal communication",
                    "Emojis are inappropriate in client-facing emails",
                    "pro-emoji"
            ));
            score -= 10;
        }

        score = Math.max(Math.min(score, 100), 0);
        CorrectionScore correctionScore = CorrectionScore.of(0, 0, 0, 0, 0, score);
        return new CorrectionStepResult(correctionScore, annotations);
    }
}
