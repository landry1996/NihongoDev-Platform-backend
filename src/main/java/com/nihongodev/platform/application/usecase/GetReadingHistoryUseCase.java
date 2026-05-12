package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.dto.ContentReadingSessionDto;
import com.nihongodev.platform.application.port.in.GetReadingHistoryPort;
import com.nihongodev.platform.application.port.out.ContentReadingSessionRepositoryPort;
import com.nihongodev.platform.application.port.out.RealContentRepositoryPort;
import com.nihongodev.platform.domain.model.ContentReadingSession;
import com.nihongodev.platform.domain.model.RealContent;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class GetReadingHistoryUseCase implements GetReadingHistoryPort {

    private final ContentReadingSessionRepositoryPort sessionRepository;
    private final RealContentRepositoryPort contentRepository;

    public GetReadingHistoryUseCase(ContentReadingSessionRepositoryPort sessionRepository,
                                    RealContentRepositoryPort contentRepository) {
        this.sessionRepository = sessionRepository;
        this.contentRepository = contentRepository;
    }

    @Override
    public List<ContentReadingSessionDto> execute(UUID userId) {
        List<ContentReadingSession> sessions = sessionRepository.findByUserId(userId);
        return sessions.stream().map(session -> {
            String title = contentRepository.findById(session.getContentId())
                .map(RealContent::getTitle)
                .orElse("Unknown");
            return RealContentMapper.toSessionDto(session, title);
        }).toList();
    }
}
