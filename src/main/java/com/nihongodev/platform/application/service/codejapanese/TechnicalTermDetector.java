package com.nihongodev.platform.application.service.codejapanese;

import com.nihongodev.platform.domain.model.CodeContext;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class TechnicalTermDetector {

    private static final Map<String, String> TECH_TERMS_JP = Map.ofEntries(
        Map.entry("authentication", "認証"),
        Map.entry("authorization", "認可"),
        Map.entry("database", "データベース"),
        Map.entry("endpoint", "エンドポイント"),
        Map.entry("refactoring", "リファクタリング"),
        Map.entry("deployment", "デプロイ"),
        Map.entry("performance", "パフォーマンス"),
        Map.entry("dependency", "依存関係"),
        Map.entry("middleware", "ミドルウェア"),
        Map.entry("concurrency", "並行処理"),
        Map.entry("cache", "キャッシュ"),
        Map.entry("migration", "マイグレーション"),
        Map.entry("rollback", "ロールバック"),
        Map.entry("null check", "nullチェック"),
        Map.entry("exception", "例外"),
        Map.entry("unit test", "ユニットテスト"),
        Map.entry("integration test", "結合テスト"),
        Map.entry("code review", "コードレビュー"),
        Map.entry("pull request", "プルリクエスト"),
        Map.entry("bug", "バグ"),
        Map.entry("hotfix", "ホットフィックス")
    );

    public int evaluateTermUsage(String text, CodeContext context) {
        int score = 70;
        int termsFound = 0;

        for (String jpTerm : TECH_TERMS_JP.values()) {
            if (text.contains(jpTerm)) {
                termsFound++;
            }
        }

        if (termsFound >= 3) score += 20;
        else if (termsFound >= 1) score += 10;

        for (String engTerm : TECH_TERMS_JP.keySet()) {
            if (text.toLowerCase().contains(engTerm) && !text.contains(TECH_TERMS_JP.get(engTerm))) {
                score -= 10;
            }
        }

        return Math.max(0, Math.min(100, score));
    }

    public Map<String, String> getTechTerms() {
        return TECH_TERMS_JP;
    }
}
