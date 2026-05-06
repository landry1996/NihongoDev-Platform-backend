package com.nihongodev.platform.application.port.in;

import com.nihongodev.platform.application.command.SearchVocabularyCommand;
import com.nihongodev.platform.application.dto.VocabularyDto;

import java.util.List;
import java.util.UUID;

public interface SearchVocabularyPort {
    VocabularyDto getById(UUID id);
    List<VocabularyDto> search(SearchVocabularyCommand command);
    List<VocabularyDto> getByLessonId(UUID lessonId);
}
