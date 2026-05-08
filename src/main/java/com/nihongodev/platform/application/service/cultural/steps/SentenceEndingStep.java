package com.nihongodev.platform.application.service.cultural.steps;

import com.nihongodev.platform.application.service.cultural.KeigoAnalysisStep;
import com.nihongodev.platform.domain.model.KeigoLevel;
import com.nihongodev.platform.domain.model.KeigoViolation;
import com.nihongodev.platform.domain.model.RelationshipType;
import com.nihongodev.platform.domain.model.Severity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class SentenceEndingStep implements KeigoAnalysisStep {

    private static final Pattern CASUAL_ENDING = Pattern.compile("(だ[。？！\\s]|だね|だよ|じゃん|っす)");
    private static final Pattern DESU_MASU = Pattern.compile("(です|ます|ました|ません|でした|でしょうか)");

    @Override
    public List<KeigoViolation> analyze(String text, KeigoLevel expectedLevel, RelationshipType relationship) {
        List<KeigoViolation> violations = new ArrayList<>();

        if (expectedLevel == KeigoLevel.CASUAL) {
            return violations;
        }

        Matcher casualMatcher = CASUAL_ENDING.matcher(text);
        while (casualMatcher.find()) {
            String found = casualMatcher.group();
            violations.add(new KeigoViolation(
                    found, found.replace("だ", "です").replace("じゃん", "ですね").replace("っす", "です"),
                    KeigoLevel.CASUAL, expectedLevel,
                    "casual_sentence_ending", Severity.MODERATE
            ));
        }

        if ((expectedLevel == KeigoLevel.SONKEIGO || expectedLevel == KeigoLevel.KENJOUGO) && !DESU_MASU.matcher(text).find()) {
            if (!text.contains("ございます") && !text.contains("存じます") && !text.contains("いたします")) {
                violations.add(new KeigoViolation(
                        text.length() > 20 ? text.substring(text.length() - 20) : text,
                        "...ございます / ...いたします",
                        KeigoLevel.CASUAL, expectedLevel,
                        "missing_polite_ending", Severity.MODERATE
                ));
            }
        }

        return violations;
    }
}
