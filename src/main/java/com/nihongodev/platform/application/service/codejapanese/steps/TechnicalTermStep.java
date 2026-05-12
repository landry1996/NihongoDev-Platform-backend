package com.nihongodev.platform.application.service.codejapanese.steps;

import com.nihongodev.platform.application.service.codejapanese.CommitAnalysisStep;
import com.nihongodev.platform.domain.model.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class TechnicalTermStep implements CommitAnalysisStep {

    private static final Map<String, String> COMMON_MISTAKES = Map.ofEntries(
        Map.entry("オセンティケーション", "認証"),
        Map.entry("データーベース", "データベース"),
        Map.entry("ファンクション", "関数"),
        Map.entry("アプリケイション", "アプリケーション"),
        Map.entry("インターフェイス", "インターフェース"),
        Map.entry("サーバ", "サーバー"),
        Map.entry("ユーザ", "ユーザー")
    );

    @Override
    public List<TechnicalJapaneseViolation> analyze(String commitMessage, CommitMessageRule rule) {
        List<TechnicalJapaneseViolation> violations = new ArrayList<>();

        for (Map.Entry<String, String> entry : COMMON_MISTAKES.entrySet()) {
            if (commitMessage.contains(entry.getKey())) {
                violations.add(new TechnicalJapaneseViolation(
                    entry.getKey(),
                    entry.getValue(),
                    ViolationType.KATAKANA_MISUSE,
                    "Incorrect technical term: use " + entry.getValue(),
                    Severity.MINOR
                ));
            }
        }

        return violations;
    }
}
