package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.port.in.DeleteVocabularyPort;
import com.nihongodev.platform.application.port.out.VocabularyRepositoryPort;
import com.nihongodev.platform.domain.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class DeleteVocabularyUseCase implements DeleteVocabularyPort {

    private final VocabularyRepositoryPort vocabularyRepository;

    public DeleteVocabularyUseCase(VocabularyRepositoryPort vocabularyRepository) {
        this.vocabularyRepository = vocabularyRepository;
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        if (!vocabularyRepository.existsById(id)) {
            throw new ResourceNotFoundException("Vocabulary", "id", id);
        }
        vocabularyRepository.deleteById(id);
    }
}
