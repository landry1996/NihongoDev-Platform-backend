package com.nihongodev.platform.application.service.correction;

import com.nihongodev.platform.domain.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VocabularyStep implements CorrectionStep {

    private static final Map<String, String> IT_VOCABULARY_SUGGESTIONS = Map.ofEntries(
            Map.entry("バグ", "不具合 (formal) or バグ (casual)"),
            Map.entry("直す", "修正する (more professional in IT context)"),
            Map.entry("やる", "実施する or 対応する (more professional)"),
            Map.entry("見る", "確認する or レビューする (in code review context)"),
            Map.entry("分かる", "把握する or 理解する (more formal)"),
            Map.entry("使う", "利用する or 活用する (more professional)"),
            Map.entry("作る", "作成する or 実装する (in dev context)"),
            Map.entry("変える", "変更する (more formal in business)")
    );

    private static final List<String> ADVANCED_VOCABULARY = List.of(
            "実装", "設計", "仕様", "要件", "リファクタリング",
            "デプロイ", "テスト", "レビュー", "マージ", "プルリクエスト"
    );

    @Override
    public CorrectionStepResult process(String text, CorrectionContext context) {
        List<Annotation> annotations = new ArrayList<>();
        double score = 70.0;

        for (Map.Entry<String, String> entry : IT_VOCABULARY_SUGGESTIONS.entrySet()) {
            String casual = entry.getKey();
            if (context.getTextType() == TextType.SLACK_MESSAGE
                    || context.getTextType() == TextType.COMMIT_MESSAGE) {
                continue;
            }
            Pattern pattern = Pattern.compile(Pattern.quote(casual));
            Matcher matcher = pattern.matcher(text);
            while (matcher.find()) {
                annotations.add(Annotation.create(
                        matcher.start(), matcher.end(), Severity.INFO,
                        AnnotationCategory.VOCABULARY, casual,
                        entry.getValue(),
                        "Consider using a more professional alternative in this context",
                        "vocab-casual-" + casual
                ));
                score -= 3;
            }
        }

        int advancedCount = 0;
        for (String word : ADVANCED_VOCABULARY) {
            if (text.contains(word)) advancedCount++;
        }
        score += Math.min(advancedCount * 5, 30);
        score = Math.max(Math.min(score, 100), 0);

        CorrectionScore correctionScore = CorrectionScore.of(0, score, 0, 0, 0, 0);
        return new CorrectionStepResult(correctionScore, annotations);
    }
}
