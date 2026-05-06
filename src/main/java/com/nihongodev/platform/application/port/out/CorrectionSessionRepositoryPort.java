package com.nihongodev.platform.application.port.out;

import com.nihongodev.platform.domain.model.CorrectionSession;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CorrectionSessionRepositoryPort {
    CorrectionSession save(CorrectionSession session);
    Optional<CorrectionSession> findById(UUID id);
    List<CorrectionSession> findByUserId(UUID userId);
    int countByUserId(UUID userId);
}
