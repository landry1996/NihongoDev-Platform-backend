package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.dto.LessonDto;
import com.nihongodev.platform.application.port.in.GetLessonPort;
import com.nihongodev.platform.application.port.out.LessonRepositoryPort;
import com.nihongodev.platform.domain.exception.ResourceNotFoundException;
import com.nihongodev.platform.domain.model.Lesson;
import com.nihongodev.platform.domain.model.LessonLevel;
import com.nihongodev.platform.domain.model.LessonType;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class GetLessonUseCase implements GetLessonPort {

    private final LessonRepositoryPort lessonRepository;

    public GetLessonUseCase(LessonRepositoryPort lessonRepository) {
        this.lessonRepository = lessonRepository;
    }

    @Override
    public LessonDto getById(UUID id) {
        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson", "id", id));
        return mapToDto(lesson);
    }

    @Override
    public List<LessonDto> getAll() {
        return lessonRepository.findAll().stream().map(this::mapToDto).toList();
    }

    @Override
    public List<LessonDto> getByType(LessonType type) {
        return lessonRepository.findByType(type).stream().map(this::mapToDto).toList();
    }

    @Override
    public List<LessonDto> getByLevel(LessonLevel level) {
        return lessonRepository.findByLevel(level).stream().map(this::mapToDto).toList();
    }

    @Override
    public List<LessonDto> getByTypeAndLevel(LessonType type, LessonLevel level) {
        return lessonRepository.findByTypeAndLevel(type, level).stream().map(this::mapToDto).toList();
    }

    @Override
    public List<LessonDto> getPublished() {
        return lessonRepository.findPublished().stream().map(this::mapToDto).toList();
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
