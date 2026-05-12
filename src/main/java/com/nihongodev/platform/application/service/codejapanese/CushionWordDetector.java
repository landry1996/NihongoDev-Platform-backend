package com.nihongodev.platform.application.service.codejapanese;

import com.nihongodev.platform.domain.model.ReviewLevel;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CushionWordDetector {

    private static final List<String> REVIEW_CUSHION_WORDS = List.of(
        "恐縮ですが",
        "お手数ですが",
        "もし可能であれば",
        "確認なのですが",
        "念のため",
        "細かい点ですが",
        "個人的な意見ですが",
        "ご検討いただけると"
    );

    public int countCushionWords(String text) {
        int count = 0;
        for (String word : REVIEW_CUSHION_WORDS) {
            if (text.contains(word)) {
                count++;
            }
        }
        return count;
    }

    public int scoreCushionUsage(String text, ReviewLevel expectedLevel) {
        int count = countCushionWords(text);

        if (expectedLevel == ReviewLevel.SUGGESTION || expectedLevel == ReviewLevel.QUESTION) {
            if (count >= 2) return 100;
            if (count == 1) return 75;
            return 30;
        }

        if (expectedLevel == ReviewLevel.MUST_FIX) {
            if (count >= 1) return 90;
            return 60;
        }

        if (expectedLevel == ReviewLevel.PRAISE) {
            return 80;
        }

        if (count >= 1) return 80;
        return 50;
    }
}
