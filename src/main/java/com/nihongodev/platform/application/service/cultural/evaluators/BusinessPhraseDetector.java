package com.nihongodev.platform.application.service.cultural.evaluators;

import com.nihongodev.platform.domain.model.WorkplaceContext;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BusinessPhraseDetector {

    private static final List<String> BUSINESS_PHRASES = List.of(
            "お世話になっております", "よろしくお願いいたします",
            "ご確認ください", "ご連絡いたします",
            "承知いたしました", "かしこまりました",
            "ご検討のほど", "何卒よろしくお願い申し上げます",
            "ご査収ください", "お忙しいところ恐れ入りますが",
            "ご対応いただき", "ご理解賜りますよう"
    );

    private static final List<String> EMAIL_SPECIFIC = List.of(
            "お世話になっております", "ご査収ください",
            "以上、よろしくお願いいたします", "取り急ぎご連絡"
    );

    public int score(String text, WorkplaceContext context) {
        int score = 40;

        long businessCount = BUSINESS_PHRASES.stream().filter(text::contains).count();
        score += (int) (businessCount * 12);

        if (context == WorkplaceContext.EMAIL) {
            long emailCount = EMAIL_SPECIFIC.stream().filter(text::contains).count();
            score += (int) (emailCount * 10);
        }

        return Math.max(0, Math.min(100, score));
    }
}
