package com.nihongodev.platform.application.port.in;

import com.nihongodev.platform.application.command.SubmitInterviewAnswerCommand;
import com.nihongodev.platform.application.dto.InterviewAnswerResultDto;

import java.util.UUID;

public interface SubmitInterviewAnswerPort {
    InterviewAnswerResultDto submit(UUID userId, SubmitInterviewAnswerCommand command);
}
