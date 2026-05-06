package com.nihongodev.platform.application.port.out;

import com.nihongodev.platform.domain.model.Vocabulary;
import com.nihongodev.platform.domain.model.VocabularyCategory;
import com.nihongodev.platform.domain.model.VocabularyLevel;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VocabularyRepositoryPort {
    Vocabulary save(Vocabulary vocabulary);
    List<Vocabulary> saveAll(List<Vocabulary> vocabularies);
    Optional<Vocabulary> findById(UUID id);
    List<Vocabulary> findAll();
    List<Vocabulary> findByCategory(VocabularyCategory category);
    List<Vocabulary> findByLevel(VocabularyLevel level);
    List<Vocabulary> findByCategoryAndLevel(VocabularyCategory category, VocabularyLevel level);
    List<Vocabulary> findByLessonId(UUID lessonId);
    List<Vocabulary> search(String query, VocabularyCategory category, VocabularyLevel level, UUID lessonId, String tag);
    void deleteById(UUID id);
    boolean existsById(UUID id);
}
