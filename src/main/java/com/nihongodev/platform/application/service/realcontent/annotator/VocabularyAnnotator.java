package com.nihongodev.platform.application.service.realcontent.annotator;

import com.nihongodev.platform.domain.model.AnnotationType;
import com.nihongodev.platform.domain.model.ContentAnnotation;
import com.nihongodev.platform.domain.model.JapaneseLevel;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class VocabularyAnnotator implements ContentAnnotator {

    private static final Map<String, VocabEntry> TECH_VOCABULARY = Map.ofEntries(
        Map.entry("実装", new VocabEntry("じっそう", "implementation", JapaneseLevel.N3)),
        Map.entry("設計", new VocabEntry("せっけい", "design/architecture", JapaneseLevel.N3)),
        Map.entry("変数", new VocabEntry("へんすう", "variable", JapaneseLevel.N2)),
        Map.entry("関数", new VocabEntry("かんすう", "function", JapaneseLevel.N2)),
        Map.entry("配列", new VocabEntry("はいれつ", "array", JapaneseLevel.N2)),
        Map.entry("条件分岐", new VocabEntry("じょうけんぶんき", "conditional branching", JapaneseLevel.N2)),
        Map.entry("環境変数", new VocabEntry("かんきょうへんすう", "environment variable", JapaneseLevel.N2)),
        Map.entry("依存関係", new VocabEntry("いぞんかんけい", "dependency", JapaneseLevel.N1)),
        Map.entry("非同期", new VocabEntry("ひどうき", "asynchronous", JapaneseLevel.N1)),
        Map.entry("冗長化", new VocabEntry("じょうちょうか", "redundancy", JapaneseLevel.N1)),
        Map.entry("脆弱性", new VocabEntry("ぜいじゃくせい", "vulnerability", JapaneseLevel.N1)),
        Map.entry("可読性", new VocabEntry("かどくせい", "readability", JapaneseLevel.N1)),
        Map.entry("保守性", new VocabEntry("ほしゅせい", "maintainability", JapaneseLevel.N1)),
        Map.entry("拡張性", new VocabEntry("かくちょうせい", "extensibility/scalability", JapaneseLevel.N1)),
        Map.entry("リファクタリング", new VocabEntry("リファクタリング", "refactoring", JapaneseLevel.N3)),
        Map.entry("デプロイ", new VocabEntry("デプロイ", "deploy", JapaneseLevel.N3)),
        Map.entry("マージ", new VocabEntry("マージ", "merge", JapaneseLevel.N3)),
        Map.entry("コードレビュー", new VocabEntry("コードレビュー", "code review", JapaneseLevel.N3)),
        Map.entry("プルリクエスト", new VocabEntry("プルリクエスト", "pull request", JapaneseLevel.N3)),
        Map.entry("不具合", new VocabEntry("ふぐあい", "bug/defect", JapaneseLevel.N2))
    );

    @Override
    public AnnotationType getType() {
        return AnnotationType.VOCABULARY;
    }

    @Override
    public List<ContentAnnotation> annotate(String text) {
        List<ContentAnnotation> annotations = new ArrayList<>();
        for (Map.Entry<String, VocabEntry> entry : TECH_VOCABULARY.entrySet()) {
            String word = entry.getKey();
            Pattern pattern = Pattern.compile(Pattern.quote(word));
            Matcher matcher = pattern.matcher(text);
            while (matcher.find()) {
                VocabEntry vocab = entry.getValue();
                annotations.add(ContentAnnotation.create(
                    matcher.start(), matcher.end(), word,
                    vocab.reading(), vocab.meaning(), AnnotationType.VOCABULARY, vocab.level()
                ));
                break;
            }
        }
        return annotations;
    }

    private record VocabEntry(String reading, String meaning, JapaneseLevel level) {}
}
