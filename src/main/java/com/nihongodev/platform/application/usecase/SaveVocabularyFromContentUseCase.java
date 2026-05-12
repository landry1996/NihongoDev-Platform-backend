package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.port.in.SaveVocabularyFromContentPort;
import com.nihongodev.platform.application.port.out.ContentReadingSessionRepositoryPort;
import com.nihongodev.platform.domain.exception.ResourceNotFoundException;
import com.nihongodev.platform.domain.model.ContentReadingSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class SaveVocabularyFromContentUseCase implements SaveVocabularyFromContentPort {

    private final ContentReadingSessionRepositoryPort sessionRepository;

    public SaveVocabularyFromContentUseCase(ContentReadingSessionRepositoryPort sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    @Override
    @Transactional
    public void execute(UUID userId, UUID sessionId, UUID annotationId) {
        ContentReadingSession session = sessionRepository.findById(sessionId)
            .orElseThrow(() -> new ResourceNotFoundException("ContentReadingSession", "id", sessionId));

        session.saveVocabulary(annotationId);
        sessionRepository.save(session);
    }
}
