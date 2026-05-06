package com.nihongodev.platform.application.port.in;

import com.nihongodev.platform.application.command.SubmitAnswerCommand;
import com.nihongodev.platform.application.dto.AnswerResultDto;

public interface SubmitAnswerPort {
    AnswerResultDto submit(SubmitAnswerCommand command);
}
