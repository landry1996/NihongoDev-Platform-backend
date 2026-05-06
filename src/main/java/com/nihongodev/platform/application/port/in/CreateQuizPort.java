package com.nihongodev.platform.application.port.in;

import com.nihongodev.platform.application.command.AddQuestionCommand;
import com.nihongodev.platform.application.command.CreateQuizCommand;
import com.nihongodev.platform.application.dto.QuestionDto;
import com.nihongodev.platform.application.dto.QuizDto;

public interface CreateQuizPort {
    QuizDto create(CreateQuizCommand command);
    QuestionDto addQuestion(AddQuestionCommand command);
}
