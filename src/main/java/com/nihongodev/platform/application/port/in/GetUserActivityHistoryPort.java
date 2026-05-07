package com.nihongodev.platform.application.port.in;

import com.nihongodev.platform.application.dto.LearningActivityDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;

public interface GetUserActivityHistoryPort {
    Page<LearningActivityDto> execute(UUID userId, Pageable pageable);
}
