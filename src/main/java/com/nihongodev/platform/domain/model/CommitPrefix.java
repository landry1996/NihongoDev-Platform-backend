package com.nihongodev.platform.domain.model;

public enum CommitPrefix {
    FEAT("feat", "機能追加"),
    FIX("fix", "バグ修正"),
    REFACTOR("refactor", "リファクタリング"),
    DOCS("docs", "ドキュメント修正"),
    TEST("test", "テスト追加"),
    PERF("perf", "パフォーマンス改善"),
    CHORE("chore", "雑務"),
    STYLE("style", "スタイル修正"),
    CI("ci", "CI/CD修正");

    private final String english;
    private final String japanese;

    CommitPrefix(String english, String japanese) {
        this.english = english;
        this.japanese = japanese;
    }

    public String getEnglish() { return english; }
    public String getJapanese() { return japanese; }
}
