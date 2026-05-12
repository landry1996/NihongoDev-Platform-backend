package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.command.CompleteReadingSessionCommand;
import com.nihongodev.platform.application.dto.ContentReadingSessionDto;
import com.nihongodev.platform.application.port.in.CompleteReadingSessionPort;
import com.nihongodev.platform.application.port.out.ContentReadingSessionRepositoryPort;
import com.nihongodev.platform.application.port.out.EventPublisherPort;
import com.nihongodev.platform.application.port.out.RealContentRepositoryPort;
import com.nihongodev.platform.domain.event.ContentReadCompletedEvent;
import com.nihongodev.platform.domain.exception.ResourceNotFoundException;
import com.nihongodev.platform.domain.model.ContentReadingSession;
import com.nihongodev.platform.domain.model.RealContent;

import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CompleteReadingSessionUseCase implements CompleteReadingSessionPort {

    private final ContentReadingSessionRepositoryPort sessionRepository;
    private final RealContentRepositoryPort contentRepository;
    private final EventPublisherPort eventPublisher;

    public CompleteReadingSessionUseCase(ContentReadingSessionRepositoryPort sessionRepository,
                                         RealContentRepositoryPort contentRepository,
                                         EventPublisherPort eventPublisher) {
        this.sessionRepository = sessionRepository;
        this.contentRepository = contentRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public ContentReadingSessionDto execute(CompleteReadingSessionCommand command) {
        ContentReadingSession session = sessionRepository.findById(command.sessionId())
            .orElseThrow(() -> new ResourceNotFoundException("ContentReadingSession", "id", command.sessionId()));

        session.complete(command.readingTimeSeconds(), command.annotationsViewed(),
            command.vocabularyLookedUp(), command.comprehensionScore());

        ContentReadingSession savedSession = sessionRepository.save(session);

        UUID contentId = savedSession.getContentId();
        RealContent content = contentRepository.findById(contentId)
            .orElseThrow(() -> new ResourceNotFoundException("RealContent", "id", contentId));

        eventPublisher.publish("real-content-events", ContentReadCompletedEvent.create(
            command.userId(), savedSession.getContentId(), savedSession.getId(),
            command.readingTimeSeconds(), command.comprehensionScore(),
            savedSession.getSavedVocabulary().size()
        ));

        return RealContentMapper.toSessionDto(savedSession, content.getTitle());
    }
}
