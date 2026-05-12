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
public class TechnicalTermAnnotator implements ContentAnnotator {

    private static final Map<String, TechTerm> TECH_TERMS = Map.ofEntries(
        Map.entry("マイクロサービス", new TechTerm("マイクロサービス", "microservice architecture pattern", JapaneseLevel.N3)),
        Map.entry("コンテナ", new TechTerm("コンテナ", "container (Docker/OCI)", JapaneseLevel.N3)),
        Map.entry("ロードバランサー", new TechTerm("ロードバランサー", "load balancer", JapaneseLevel.N3)),
        Map.entry("ミドルウェア", new TechTerm("ミドルウェア", "middleware", JapaneseLevel.N3)),
        Map.entry("フレームワーク", new TechTerm("フレームワーク", "framework", JapaneseLevel.N3)),
        Map.entry("永続化", new TechTerm("えいぞくか", "persistence (data storage)", JapaneseLevel.N2)),
        Map.entry("直列化", new TechTerm("ちょくれつか", "serialization", JapaneseLevel.N1)),
        Map.entry("抽象化", new TechTerm("ちゅうしょうか", "abstraction", JapaneseLevel.N2)),
        Map.entry("疎結合", new TechTerm("そけつごう", "loose coupling", JapaneseLevel.N1)),
        Map.entry("密結合", new TechTerm("みつけつごう", "tight coupling", JapaneseLevel.N1)),
        Map.entry("単体テスト", new TechTerm("たんたいテスト", "unit test", JapaneseLevel.N2)),
        Map.entry("結合テスト", new TechTerm("けつごうテスト", "integration test", JapaneseLevel.N2)),
        Map.entry("負荷テスト", new TechTerm("ふかテスト", "load test", JapaneseLevel.N2)),
        Map.entry("バージョン管理", new TechTerm("バージョンかんり", "version control", JapaneseLevel.N3)),
        Map.entry("継続的インテグレーション", new TechTerm("けいぞくてきインテグレーション", "continuous integration (CI)", JapaneseLevel.N2))
    );

    @Override
    public AnnotationType getType() {
        return AnnotationType.TECHNICAL_TERM;
    }

    @Override
    public List<ContentAnnotation> annotate(String text) {
        List<ContentAnnotation> annotations = new ArrayList<>();
        for (Map.Entry<String, TechTerm> entry : TECH_TERMS.entrySet()) {
            String term = entry.getKey();
            Pattern pattern = Pattern.compile(Pattern.quote(term));
            Matcher matcher = pattern.matcher(text);
            while (matcher.find()) {
                TechTerm tt = entry.getValue();
                annotations.add(ContentAnnotation.create(
                    matcher.start(), matcher.end(), term,
                    tt.reading(), tt.meaning(), AnnotationType.TECHNICAL_TERM, tt.level()
                ));
                break;
            }
        }
        return annotations;
    }

    private record TechTerm(String reading, String meaning, JapaneseLevel level) {}
}
