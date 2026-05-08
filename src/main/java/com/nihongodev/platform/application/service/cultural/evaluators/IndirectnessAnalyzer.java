package com.nihongodev.platform.application.service.cultural.evaluators;

import com.nihongodev.platform.domain.model.WorkplaceContext;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class IndirectnessAnalyzer {

    private static final List<String> CUSHION_WORDS = List.of(
            "恐れ入りますが", "申し訳ございませんが", "お忙しいところ",
            "恐縮ですが", "差し支えなければ", "もしよろしければ",
            "大変恐縮ですが", "お手数をおかけしますが"
    );

    private static final List<String> HEDGING_EXPRESSIONS = List.of(
            "かもしれません", "と思いますが", "ではないかと",
            "可能であれば", "ご検討いただければ"
    );

    private static final List<String> NEGATIVE_QUESTION = List.of(
            "いただけないでしょうか", "いただけませんでしょうか",
            "ていただけますか", "ていただけませんか"
    );

    private static final List<String> DIRECT_REFUSALS = List.of(
            "できません", "無理です", "やりません", "嫌です", "ダメです"
    );

    private static final List<String> BLUNT_ASSERTIONS = List.of(
            "やってください", "してください", "しろ", "やれ"
    );

    public int score(String text, WorkplaceContext context) {
        int score = 50;

        long cushions = CUSHION_WORDS.stream().filter(text::contains).count();
        score += (int) (cushions * 15);

        long hedging = HEDGING_EXPRESSIONS.stream().filter(text::contains).count();
        score += (int) (hedging * 10);

        long negativeQ = NEGATIVE_QUESTION.stream().filter(text::contains).count();
        score += (int) (negativeQ * 10);

        long directRefusals = DIRECT_REFUSALS.stream().filter(text::contains).count();
        score -= (int) (directRefusals * 20);

        long blunt = BLUNT_ASSERTIONS.stream().filter(text::contains).count();
        score -= (int) (blunt * 15);

        if (context == WorkplaceContext.CONFLICT || context == WorkplaceContext.EMAIL) {
            score += 5;
        }

        return Math.max(0, Math.min(100, score));
    }
}
