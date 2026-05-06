package com.nihongodev.platform.application.port.in;

import com.nihongodev.platform.application.dto.LessonDto;
import com.nihongodev.platform.domain.model.LessonLevel;
import com.nihongodev.platform.domain.model.LessonType;

import java.util.List;
import java.util.UUID;

public interface GetLessonPort {
    LessonDto getById(UUID id);
    List<LessonDto> getAll();
    List<LessonDto> getByType(LessonType type);
    List<LessonDto> getByLevel(LessonLevel level);
    List<LessonDto> getByTypeAndLevel(LessonType type, LessonLevel level);
    List<LessonDto> getPublished();
}
