package com.nihongodev.platform.application.port.in;

import com.nihongodev.platform.application.command.UpdateVocabularyCommand;
import com.nihongodev.platform.application.dto.VocabularyDto;

public interface UpdateVocabularyPort {
    VocabularyDto update(UpdateVocabularyCommand command);
}
