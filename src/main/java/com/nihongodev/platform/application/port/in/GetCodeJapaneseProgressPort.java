package com.nihongodev.platform.application.port.in;

import com.nihongodev.platform.application.dto.CodeJapaneseProgressDto;

import java.util.List;
import java.util.UUID;

public interface GetCodeJapaneseProgressPort {
    List<CodeJapaneseProgressDto> getByUserId(UUID userId);
}
