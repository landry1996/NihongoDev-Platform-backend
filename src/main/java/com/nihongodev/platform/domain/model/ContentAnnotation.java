package com.nihongodev.platform.domain.model;

import java.util.UUID;

public record ContentAnnotation(
    UUID id,
    int startOffset,
    int endOffset,
    String surfaceForm,
    String reading,
    String meaning,
    AnnotationType annotationType,
    JapaneseLevel requiredLevel,
    String grammarNote,
    String culturalNote
) {
    public static ContentAnnotation create(int startOffset, int endOffset, String surfaceForm,
                                           String reading, String meaning, AnnotationType type,
                                           JapaneseLevel level) {
        return new ContentAnnotation(UUID.randomUUID(), startOffset, endOffset, surfaceForm,
            reading, meaning, type, level, null, null);
    }

    public ContentAnnotation withGrammarNote(String note) {
        return new ContentAnnotation(id, startOffset, endOffset, surfaceForm, reading, meaning,
            annotationType, requiredLevel, note, culturalNote);
    }

    public ContentAnnotation withCulturalNote(String note) {
        return new ContentAnnotation(id, startOffset, endOffset, surfaceForm, reading, meaning,
            annotationType, requiredLevel, grammarNote, note);
    }
}
