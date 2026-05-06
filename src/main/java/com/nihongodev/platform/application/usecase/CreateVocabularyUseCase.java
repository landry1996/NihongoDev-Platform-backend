package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.command.CreateVocabularyCommand;
import com.nihongodev.platform.application.dto.VocabularyDto;
import com.nihongodev.platform.application.port.in.CreateVocabularyPort;
import com.nihongodev.platform.application.port.out.VocabularyRepositoryPort;
import com.nihongodev.platform.domain.model.Vocabulary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CreateVocabularyUseCase implements CreateVocabularyPort {

    private final VocabularyRepositoryPort vocabularyRepository;

    public CreateVocabularyUseCase(VocabularyRepositoryPort vocabularyRepository) {
        this.vocabularyRepository = vocabularyRepository;
    }

    @Override
    @Transactional
    public VocabularyDto create(CreateVocabularyCommand command) {
        Vocabulary vocabulary = Vocabulary.create(
                command.french(), command.english(), command.japanese(), command.romaji(),
                command.example(), command.codeExample(), command.category(),
                command.level(), command.domain(), command.tags(), command.lessonId()
        );
        Vocabulary saved = vocabularyRepository.save(vocabulary);
        return mapToDto(saved);
    }

    @Override
    @Transactional
    public List<VocabularyDto> batchImport(List<CreateVocabularyCommand> commands) {
        List<Vocabulary> vocabularies = commands.stream()
                .map(cmd -> Vocabulary.create(
                        cmd.french(), cmd.english(), cmd.japanese(), cmd.romaji(),
                        cmd.example(), cmd.codeExample(), cmd.category(),
                        cmd.level(), cmd.domain(), cmd.tags(), cmd.lessonId()
                ))
                .toList();
        List<Vocabulary> saved = vocabularyRepository.saveAll(vocabularies);
        return saved.stream().map(this::mapToDto).toList();
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
