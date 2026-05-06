package com.nihongodev.platform.infrastructure.persistence.specification;

import com.nihongodev.platform.infrastructure.persistence.entity.VocabularyEntity;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public final class VocabularySpecification {

    private VocabularySpecification() {}

    public static Specification<VocabularyEntity> hasCategory(String category) {
        return (root, query, cb) -> category == null ? null : cb.equal(root.get("category"), category);
    }

    public static Specification<VocabularyEntity> hasLevel(String level) {
        return (root, query, cb) -> level == null ? null : cb.equal(root.get("level"), level);
    }

    public static Specification<VocabularyEntity> hasLessonId(UUID lessonId) {
        return (root, query, cb) -> lessonId == null ? null : cb.equal(root.get("lessonId"), lessonId);
    }

    public static Specification<VocabularyEntity> hasTag(String tag) {
        return (root, query, cb) -> tag == null ? null : cb.like(cb.lower(root.get("tags")), "%" + tag.toLowerCase() + "%");
    }

    public static Specification<VocabularyEntity> textSearch(String query) {
        return (root, cq, cb) -> {
            if (query == null || query.isBlank()) return null;
            String pattern = "%" + query.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("french")), pattern),
                    cb.like(cb.lower(root.get("english")), pattern),
                    cb.like(root.get("japanese"), "%" + query + "%"),
                    cb.like(cb.lower(root.get("romaji")), pattern)
            );
        };
    }
}
