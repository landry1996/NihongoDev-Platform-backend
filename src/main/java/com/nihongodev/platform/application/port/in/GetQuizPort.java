package com.nihongodev.platform.application.port.in;

import com.nihongodev.platform.application.dto.QuizDto;

import java.util.List;
import java.util.UUID;

public interface GetQuizPort {
    QuizDto getById(UUID id);
    List<QuizDto> getByLessonId(UUID lessonId);
    List<QuizDto> getPublished();
}
