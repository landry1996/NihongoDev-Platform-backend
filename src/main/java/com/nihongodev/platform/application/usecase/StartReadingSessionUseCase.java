package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.dto.ContentReadingSessionDto;
import com.nihongodev.platform.application.port.in.StartReadingSessionPort;
import com.nihongodev.platform.application.port.out.ContentReadingSessionRepositoryPort;
import com.nihongodev.platform.application.port.out.RealContentRepositoryPort;
import com.nihongodev.platform.domain.exception.ResourceNotFoundException;
import com.nihongodev.platform.domain.model.ContentReadingSession;
import com.nihongodev.platform.domain.model.RealContent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class StartReadingSessionUseCase implements StartReadingSessionPort {

    private final ContentReadingSessionRepositoryPort sessionRepository;
    private final RealContentRepositoryPort contentRepository;

    public StartReadingSessionUseCase(ContentReadingSessionRepositoryPort sessionRepository,
                                      RealContentRepositoryPort contentRepository) {
        this.sessionRepository = sessionRepository;
        this.contentRepository = contentRepository;
    }

    @Override
    @Transactional
    public ContentReadingSessionDto execute(UUID userId, UUID contentId) {
        RealContent content = contentRepository.findById(contentId)
            .orElseThrow(() -> new ResourceNotFoundException("RealContent", "id", contentId));

        ContentReadingSession session = ContentReadingSession.start(userId, contentId);
        session = sessionRepository.save(session);

        return RealContentMapper.toSessionDto(session, content.getTitle());
    }
}
