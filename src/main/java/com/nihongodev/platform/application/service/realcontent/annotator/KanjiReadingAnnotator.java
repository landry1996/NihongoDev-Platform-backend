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
public class KanjiReadingAnnotator implements ContentAnnotator {

    private static final Map<String, KanjiEntry> COMPOUND_KANJI = Map.ofEntries(
        Map.entry("処理", new KanjiEntry("しょり", "processing", JapaneseLevel.N3)),
        Map.entry("取得", new KanjiEntry("しゅとく", "acquisition/retrieval", JapaneseLevel.N2)),
        Map.entry("送信", new KanjiEntry("そうしん", "transmission/sending", JapaneseLevel.N3)),
        Map.entry("受信", new KanjiEntry("じゅしん", "reception/receiving", JapaneseLevel.N3)),
        Map.entry("接続", new KanjiEntry("せつぞく", "connection", JapaneseLevel.N3)),
        Map.entry("認証", new KanjiEntry("にんしょう", "authentication", JapaneseLevel.N2)),
        Map.entry("権限", new KanjiEntry("けんげん", "permission/authority", JapaneseLevel.N2)),
        Map.entry("設定", new KanjiEntry("せってい", "configuration/settings", JapaneseLevel.N3)),
        Map.entry("構築", new KanjiEntry("こうちく", "construction/building", JapaneseLevel.N2)),
        Map.entry("解析", new KanjiEntry("かいせき", "analysis/parsing", JapaneseLevel.N1)),
        Map.entry("複製", new KanjiEntry("ふくせい", "duplication/clone", JapaneseLevel.N2)),
        Map.entry("廃止", new KanjiEntry("はいし", "deprecation/abolishment", JapaneseLevel.N1)),
        Map.entry("排他制御", new KanjiEntry("はいたせいぎょ", "exclusive control/mutex", JapaneseLevel.N1)),
        Map.entry("並行処理", new KanjiEntry("へいこうしょり", "parallel processing", JapaneseLevel.N1)),
        Map.entry("暗号化", new KanjiEntry("あんごうか", "encryption", JapaneseLevel.N1))
    );

    @Override
    public AnnotationType getType() {
        return AnnotationType.KANJI_BREAKDOWN;
    }

    @Override
    public List<ContentAnnotation> annotate(String text) {
        List<ContentAnnotation> annotations = new ArrayList<>();
        for (Map.Entry<String, KanjiEntry> entry : COMPOUND_KANJI.entrySet()) {
            String compound = entry.getKey();
            Pattern pattern = Pattern.compile(Pattern.quote(compound));
            Matcher matcher = pattern.matcher(text);
            while (matcher.find()) {
                KanjiEntry kanji = entry.getValue();
                annotations.add(ContentAnnotation.create(
                    matcher.start(), matcher.end(), compound,
                    kanji.reading(), kanji.meaning(), AnnotationType.KANJI_BREAKDOWN, kanji.level()
                ));
                break;
            }
        }
        return annotations;
    }

    private record KanjiEntry(String reading, String meaning, JapaneseLevel level) {}
}
