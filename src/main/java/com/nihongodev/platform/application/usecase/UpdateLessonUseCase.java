package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.command.UpdateLessonCommand;
import com.nihongodev.platform.application.dto.LessonDto;
import com.nihongodev.platform.application.port.in.UpdateLessonPort;
import com.nihongodev.platform.application.port.out.LessonRepositoryPort;
import com.nihongodev.platform.domain.exception.ResourceNotFoundException;
import com.nihongodev.platform.domain.model.Lesson;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateLessonUseCase implements UpdateLessonPort {

    private final LessonRepositoryPort lessonRepository;

    public UpdateLessonUseCase(LessonRepositoryPort lessonRepository) {
        this.lessonRepository = lessonRepository;
    }

    @Override
    @Transactional
    public LessonDto update(UpdateLessonCommand command) {
        Lesson lesson = lessonRepository.findById(command.lessonId())
                .orElseThrow(() -> new ResourceNotFoundException("Lesson", "id", command.lessonId()));

        lesson.update(
                command.title(),
                command.description(),
                command.type(),
                command.level(),
                command.content(),
                command.orderIndex()
        );

        Lesson saved = lessonRepository.save(lesson);
        return mapToDto(saved);
    }

    private LessonDto mapToDto(Lesson lesson) {
        return new LessonDto(
                lesson.getId(), lesson.getTitle(), lesson.getDescription(),
                lesson.getType(), lesson.getLevel(), lesson.getContent(),
                lesson.getOrderIndex(), lesson.isPublished(),
                lesson.getCreatedAt(), lesson.getUpdatedAt()
        );
    }
}
