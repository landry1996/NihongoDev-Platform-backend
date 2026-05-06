package com.nihongodev.platform.application.port.in;

import com.nihongodev.platform.application.command.GenerateVocabularyQuizCommand;
import com.nihongodev.platform.application.dto.VocabularyQuizDto;

import java.util.UUID;

public interface GenerateVocabularyQuizPort {
    VocabularyQuizDto generate(UUID userId, GenerateVocabularyQuizCommand command);
}
