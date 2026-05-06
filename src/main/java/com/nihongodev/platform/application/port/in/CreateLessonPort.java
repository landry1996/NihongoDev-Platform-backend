package com.nihongodev.platform.application.port.in;

import com.nihongodev.platform.application.command.CreateLessonCommand;
import com.nihongodev.platform.application.dto.LessonDto;

public interface CreateLessonPort {
    LessonDto create(CreateLessonCommand command);
}
