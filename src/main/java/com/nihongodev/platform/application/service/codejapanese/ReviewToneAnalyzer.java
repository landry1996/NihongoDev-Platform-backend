package com.nihongodev.platform.application.service.codejapanese;

import com.nihongodev.platform.domain.model.ReviewLevel;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ReviewToneAnalyzer {

    private static final List<String> CUSHION_BEFORE_SUGGESTION = List.of(
        "恐縮ですが", "もし可能であれば", "細かい点ですが", "個人的な意見ですが"
    );
    private static final List<String> QUESTION_FORMS = List.of(
        "していただけますか", "いかがでしょうか", "いただけると", "でしょうか"
    );
    private static final List<String> REASON_PATTERNS = List.of(
        "のため", "ので", "理由は", "観点で"
    );
    private static final List<String> BARE_IMPERATIVES = List.of(
        "直せ", "変えろ", "消せ", "書け"
    );
    private static final List<String> TOO_CASUAL = List.of(
        "これダメだよ", "バグってる", "意味ない", "いらない"
    );

    public int evaluate(String userResponse, ReviewLevel expectedLevel) {
        int score = 50;

        for (String pattern : CUSHION_BEFORE_SUGGESTION) {
            if (userResponse.contains(pattern)) {
                score += 15;
                break;
            }
        }

        for (String pattern : QUESTION_FORMS) {
            if (userResponse.contains(pattern)) {
                score += 10;
                break;
            }
        }

        for (String pattern : REASON_PATTERNS) {
            if (userResponse.contains(pattern)) {
                score += 10;
                break;
            }
        }

        for (String pattern : BARE_IMPERATIVES) {
            if (userResponse.contains(pattern)) {
                score -= 20;
            }
        }

        for (String pattern : TOO_CASUAL) {
            if (userResponse.contains(pattern)) {
                score -= 15;
            }
        }

        if (expectedLevel == ReviewLevel.MUST_FIX && userResponse.contains("修正")) {
            score += 10;
        }

        return Math.max(0, Math.min(100, score));
    }
}
