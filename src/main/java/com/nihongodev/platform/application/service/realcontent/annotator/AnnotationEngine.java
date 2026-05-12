package com.nihongodev.platform.application.service.realcontent.annotator;

import com.nihongodev.platform.domain.model.ContentAnnotation;
import com.nihongodev.platform.domain.model.JapaneseLevel;
import com.nihongodev.platform.domain.model.ReadingDifficulty;
import com.nihongodev.platform.domain.model.RealContent;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
public class AnnotationEngine {

    private final List<ContentAnnotator> annotators;

    public AnnotationEngine(List<ContentAnnotator> annotators) {
        this.annotators = annotators;
    }

    public void annotate(RealContent content) {
        String text = content.getBody();

        for (ContentAnnotator annotator : annotators) {
            List<ContentAnnotation> annotations = annotator.annotate(text);
            annotations.forEach(content::addAnnotation);
        }

        content.setDifficulty(assessDifficulty(content));
        content.setReadingDifficulty(assessReadingDifficulty(content));
        content.markAnnotated();
    }

    private JapaneseLevel assessDifficulty(RealContent content) {
        if (content.getAnnotations().isEmpty()) {
            return JapaneseLevel.N3;
        }

        long n1Count = content.getAnnotations().stream()
            .filter(a -> a.requiredLevel() == JapaneseLevel.N1).count();
        long n2Count = content.getAnnotations().stream()
            .filter(a -> a.requiredLevel() == JapaneseLevel.N2).count();

        double totalAnnotations = content.getAnnotations().size();
        double advancedRatio = (n1Count + n2Count) / totalAnnotations;

        if (advancedRatio > 0.6) return JapaneseLevel.N1;
        if (advancedRatio > 0.3) return JapaneseLevel.N2;
        if (n1Count > 0) return JapaneseLevel.N3;
        return JapaneseLevel.N4;
    }

    private ReadingDifficulty assessReadingDifficulty(RealContent content) {
        int wordCount = content.getWordCount();
        int kanjiCount = content.getKanjiCount();
        double kanjiDensity = wordCount > 0 ? (double) kanjiCount / wordCount : 0;

        JapaneseLevel level = content.getDifficulty();

        if (level == JapaneseLevel.N1 && kanjiDensity > 0.3) return ReadingDifficulty.NATIVE_LEVEL;
        if (level == JapaneseLevel.N1 || level == JapaneseLevel.N2) return ReadingDifficulty.ADVANCED;
        if (kanjiDensity > 0.2) return ReadingDifficulty.INTERMEDIATE;
        return ReadingDifficulty.BEGINNER;
    }
}
