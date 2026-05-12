package com.nihongodev.platform.application.port.out;

import com.nihongodev.platform.domain.model.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RealContentRepositoryPort {
    RealContent save(RealContent content);
    Optional<RealContent> findById(UUID id);
    List<RealContent> findByStatus(ContentStatus status);
    List<RealContent> findPublishedByDomainAndDifficulty(ContentDomain domain, ReadingDifficulty maxDifficulty);
    List<RealContent> findPublished();
    List<RealContent> findRecentPublished(int limit);
    boolean existsBySourceUrl(String sourceUrl);
}
