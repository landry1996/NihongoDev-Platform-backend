package com.nihongodev.platform.application.port.in;

import com.nihongodev.platform.application.command.StartInterviewCommand;
import com.nihongodev.platform.application.dto.InterviewSessionDto;

import java.util.UUID;

public interface StartInterviewPort {
    InterviewSessionDto start(UUID userId, StartInterviewCommand command);
}
