package com.nihongodev.platform.application.port.in;

import com.nihongodev.platform.application.command.CreateVocabularyCommand;
import com.nihongodev.platform.application.dto.VocabularyDto;

import java.util.List;

public interface CreateVocabularyPort {
    VocabularyDto create(CreateVocabularyCommand command);
    List<VocabularyDto> batchImport(List<CreateVocabularyCommand> commands);
}
