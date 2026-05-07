package com.nihongodev.platform.application.port.out;

import com.nihongodev.platform.domain.model.CvProfile;

import java.util.Optional;
import java.util.UUID;

public interface CvProfileRepositoryPort {
    CvProfile save(CvProfile profile);
    Optional<CvProfile> findByUserId(UUID userId);
    boolean existsByUserId(UUID userId);
}
