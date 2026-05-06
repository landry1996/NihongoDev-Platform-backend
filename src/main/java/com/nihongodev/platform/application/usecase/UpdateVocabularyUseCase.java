package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.command.UpdateVocabularyCommand;
import com.nihongodev.platform.application.dto.VocabularyDto;
import com.nihongodev.platform.application.port.in.UpdateVocabularyPort;
import com.nihongodev.platform.application.port.out.VocabularyRepositoryPort;
import com.nihongodev.platform.domain.exception.ResourceNotFoundException;
import com.nihongodev.platform.domain.model.Vocabulary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateVocabularyUseCase implements UpdateVocabularyPort {

    private final VocabularyRepositoryPort vocabularyRepository;

    public UpdateVocabularyUseCase(VocabularyRepositoryPort vocabularyRepository) {
        this.vocabularyRepository = vocabularyRepository;
    }

    @Override
    @Transactional
    public VocabularyDto update(UpdateVocabularyCommand command) {
        Vocabulary vocabulary = vocabularyRepository.findById(command.vocabularyId())
                .orElseThrow(() -> new ResourceNotFoundException("Vocabulary", "id", command.vocabularyId()));

        vocabulary.update(
                command.french(), command.english(), command.japanese(), command.romaji(),
                command.example(), command.codeExample(), command.category(),
                command.level(), command.domain(), command.tags(), command.lessonId()
        );

        Vocabulary saved = vocabularyRepository.save(vocabulary);
        return mapToDto(saved);
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
