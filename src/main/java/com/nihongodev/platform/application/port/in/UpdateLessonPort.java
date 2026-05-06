package com.nihongodev.platform.application.port.in;

import com.nihongodev.platform.application.command.UpdateLessonCommand;
import com.nihongodev.platform.application.dto.LessonDto;

public interface UpdateLessonPort {
    LessonDto update(UpdateLessonCommand command);
}
