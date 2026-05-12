package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.command.CompleteReadingSessionCommand;
import com.nihongodev.platform.application.dto.ContentReadingSessionDto;
import com.nihongodev.platform.application.port.out.ContentReadingSessionRepositoryPort;
import com.nihongodev.platform.application.port.out.EventPublisherPort;
import com.nihongodev.platform.application.port.out.RealContentRepositoryPort;
import com.nihongodev.platform.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CompleteReadingSessionUseCase")
class CompleteReadingSessionUseCaseTest {

    @Mock private ContentReadingSessionRepositoryPort sessionRepository;
    @Mock private RealContentRepositoryPort contentRepository;
    @Mock private EventPublisherPort eventPublisher;

    private CompleteReadingSessionUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new CompleteReadingSessionUseCase(sessionRepository, contentRepository, eventPublisher);
    }

    @Test
    @DisplayName("should complete session and publish event")
    void shouldCompleteSession() {
        UUID userId = UUID.randomUUID();
        UUID contentId = UUID.randomUUID();
        ContentReadingSession session = ContentReadingSession.start(userId, contentId);
        UUID sessionId = session.getId();

        RealContent content = RealContent.ingest("テスト", "本文",
            "https://test.com", ContentSource.TECH_BLOG, ContentDomain.WEB_DEVELOPMENT);

        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(session));
        when(sessionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(contentRepository.findById(contentId)).thenReturn(Optional.of(content));

        var command = new CompleteReadingSessionCommand(userId, sessionId, 300, 5, 3, 85.0);

        ContentReadingSessionDto result = useCase.execute(command);

        assertThat(result.completed()).isTrue();
        assertThat(result.readingTimeSeconds()).isEqualTo(300);
        assertThat(result.comprehensionScore()).isEqualTo(85.0);
        verify(eventPublisher).publish(eq("real-content-events"), any());
    }
}
