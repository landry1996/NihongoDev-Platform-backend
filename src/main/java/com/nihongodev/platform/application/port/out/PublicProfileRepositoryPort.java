package com.nihongodev.platform.application.port.out;

import com.nihongodev.platform.domain.model.JapaneseLevel;
import com.nihongodev.platform.domain.model.PublicProfile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PublicProfileRepositoryPort {
    PublicProfile save(PublicProfile profile);
    Optional<PublicProfile> findByUserId(UUID userId);
    Optional<PublicProfile> findBySlug(String slug);
    boolean existsBySlug(String slug);
    List<PublicProfile> searchProfiles(JapaneseLevel minLevel, String skill, Boolean openToWork,
                                       int offset, int limit);
    int countSearchResults(JapaneseLevel minLevel, String skill, Boolean openToWork);
}
