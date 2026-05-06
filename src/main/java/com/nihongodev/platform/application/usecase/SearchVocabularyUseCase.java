package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.command.SearchVocabularyCommand;
import com.nihongodev.platform.application.dto.VocabularyDto;
import com.nihongodev.platform.application.port.in.SearchVocabularyPort;
import com.nihongodev.platform.application.port.out.VocabularyRepositoryPort;
import com.nihongodev.platform.domain.exception.ResourceNotFoundException;
import com.nihongodev.platform.domain.model.Vocabulary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class SearchVocabularyUseCase implements SearchVocabularyPort {

    private final VocabularyRepositoryPort vocabularyRepository;

    public SearchVocabularyUseCase(VocabularyRepositoryPort vocabularyRepository) {
        this.vocabularyRepository = vocabularyRepository;
    }

    @Override
    public VocabularyDto getById(UUID id) {
        Vocabulary vocabulary = vocabularyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vocabulary", "id", id));
        return mapToDto(vocabulary);
    }

    @Override
    public List<VocabularyDto> search(SearchVocabularyCommand command) {
        return vocabularyRepository.search(
                command.query(), command.category(), command.level(), command.lessonId(), command.tag()
        ).stream().map(this::mapToDto).toList();
    }

    @Override
    public List<VocabularyDto> getByLessonId(UUID lessonId) {
        return vocabularyRepository.findByLessonId(lessonId).stream().map(this::mapToDto).toList();
    }

    private VocabularyDto mapToDto(Vocabulary v) {
        return new VocabularyDto(
                v.getId(), v.getLessonId(), v.getFrench(), v.getEnglish(),
                v.getJapanese(), v.getRomaji(), v.getExample(), v.getCodeExample(),
                v.getCategory(), v.getLevel(), v.getDomain(), v.getTags(),
                v.getDifficultyScore(), v.getCreatedAt(), v.getUpdatedAt()
        );
    }
}
