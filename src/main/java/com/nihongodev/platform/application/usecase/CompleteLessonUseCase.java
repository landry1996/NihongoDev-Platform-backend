package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.port.in.CompleteLessonPort;
import com.nihongodev.platform.application.port.out.EventPublisherPort;
import com.nihongodev.platform.application.port.out.LessonRepositoryPort;
import com.nihongodev.platform.domain.event.LessonCompletedEvent;
import com.nihongodev.platform.domain.exception.ResourceNotFoundException;
import com.nihongodev.platform.domain.model.Lesson;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class CompleteLessonUseCase implements CompleteLessonPort {

    private final LessonRepositoryPort lessonRepository;
    private final EventPublisherPort eventPublisher;

    public CompleteLessonUseCase(LessonRepositoryPort lessonRepository, EventPublisherPort eventPublisher) {
        this.lessonRepository = lessonRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public void complete(UUID userId, UUID lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson", "id", lessonId));

        eventPublisher.publish("lesson-events", LessonCompletedEvent.of(
                userId, lesson.getId(), lesson.getTitle(),
                lesson.getType().name(), lesson.getLevel().name()
        ));
    }
}
