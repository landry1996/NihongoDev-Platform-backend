package com.nihongodev.platform.application.service.realcontent.annotator;

import com.nihongodev.platform.domain.model.AnnotationType;
import com.nihongodev.platform.domain.model.ContentAnnotation;
import com.nihongodev.platform.domain.model.JapaneseLevel;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class GrammarPatternAnnotator implements ContentAnnotator {

    private static final List<GrammarPattern> PATTERNS = List.of(
        new GrammarPattern("ことができる", "can do / is able to (formal)", JapaneseLevel.N4,
            "Potential form using こと+できる. More formal than verb potential form."),
        new GrammarPattern("ようにする", "make sure to / ensure that", JapaneseLevel.N3,
            "Expresses intention to make something a habit or ensure an outcome."),
        new GrammarPattern("において", "in / at / regarding (formal)", JapaneseLevel.N2,
            "Formal equivalent of で indicating location/context. Common in documentation."),
        new GrammarPattern("に伴い", "along with / as ~ accompanies", JapaneseLevel.N2,
            "Indicates that something changes in conjunction with something else."),
        new GrammarPattern("を踏まえて", "based on / taking into account", JapaneseLevel.N1,
            "Used in specifications to indicate building upon established facts."),
        new GrammarPattern("に基づいて", "based on / in accordance with", JapaneseLevel.N2,
            "Indicates foundation or basis for an action. Common in technical specs."),
        new GrammarPattern("とともに", "together with / at the same time as", JapaneseLevel.N2,
            "Indicates simultaneity or accompaniment."),
        new GrammarPattern("に際して", "on the occasion of / when", JapaneseLevel.N1,
            "Formal expression for timing. Common in release notes and procedures."),
        new GrammarPattern("べきである", "should / ought to", JapaneseLevel.N2,
            "Strong recommendation. Common in code review comments and guidelines."),
        new GrammarPattern("ざるを得ない", "cannot help but / have no choice but to", JapaneseLevel.N1,
            "Expresses unavoidable necessity. Used in technical decision documentation.")
    );

    @Override
    public AnnotationType getType() {
        return AnnotationType.GRAMMAR_PATTERN;
    }

    @Override
    public List<ContentAnnotation> annotate(String text) {
        List<ContentAnnotation> annotations = new ArrayList<>();
        for (GrammarPattern gp : PATTERNS) {
            Pattern pattern = Pattern.compile(Pattern.quote(gp.pattern()));
            Matcher matcher = pattern.matcher(text);
            while (matcher.find()) {
                ContentAnnotation annotation = ContentAnnotation.create(
                    matcher.start(), matcher.end(), gp.pattern(),
                    gp.pattern(), gp.meaning(), AnnotationType.GRAMMAR_PATTERN, gp.level()
                ).withGrammarNote(gp.grammarNote());
                annotations.add(annotation);
                break;
            }
        }
        return annotations;
    }

    private record GrammarPattern(String pattern, String meaning, JapaneseLevel level, String grammarNote) {}
}
