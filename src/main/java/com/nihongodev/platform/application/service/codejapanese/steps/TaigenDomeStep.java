package com.nihongodev.platform.application.service.codejapanese.steps;

import com.nihongodev.platform.application.service.codejapanese.CommitAnalysisStep;
import com.nihongodev.platform.domain.model.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TaigenDomeStep implements CommitAnalysisStep {

    private static final List<String> NOUN_ENDINGS = List.of(
        "追加", "修正", "削除", "変更", "改善", "対応", "実装", "導入",
        "更新", "整理", "統合", "分離", "移行", "設定", "構築", "最適化"
    );

    @Override
    public List<TechnicalJapaneseViolation> analyze(String commitMessage, CommitMessageRule rule) {
        List<TechnicalJapaneseViolation> violations = new ArrayList<>();

        if (!rule.requireTaigenDome()) {
            return violations;
        }

        if (!isTaigenDome(commitMessage)) {
            violations.add(new TechnicalJapaneseViolation(
                commitMessage.substring(Math.max(0, commitMessage.length() - 10)),
                "体言止め（名詞で終わること）を使用してください。例：追加、修正、実装",
                ViolationType.VERB_ENDING_IN_COMMIT,
                "Commit message must end with a noun (体言止め)",
                Severity.MODERATE
            ));
        }

        return violations;
    }

    public boolean isTaigenDome(String commitMessage) {
        String trimmed = commitMessage.trim();
        if (trimmed.endsWith("）") || trimmed.endsWith(")")) {
            int openParen = trimmed.lastIndexOf("（");
            if (openParen == -1) openParen = trimmed.lastIndexOf("(");
            if (openParen > 0) {
                trimmed = trimmed.substring(0, openParen).trim();
            }
        }
        for (String ending : NOUN_ENDINGS) {
            if (trimmed.endsWith(ending)) {
                return true;
            }
        }
        return false;
    }
}
