package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.port.in.ShowcaseBadgePort;
import com.nihongodev.platform.application.port.out.PublicProfileRepositoryPort;
import com.nihongodev.platform.application.port.out.UserBadgeRepositoryPort;
import com.nihongodev.platform.domain.exception.ResourceNotFoundException;
import com.nihongodev.platform.domain.model.PublicProfile;
import com.nihongodev.platform.domain.model.UserBadge;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ShowcaseBadgeUseCase implements ShowcaseBadgePort {

    private final UserBadgeRepositoryPort userBadgeRepository;
    private final PublicProfileRepositoryPort profileRepository;

    public ShowcaseBadgeUseCase(UserBadgeRepositoryPort userBadgeRepository,
                                PublicProfileRepositoryPort profileRepository) {
        this.userBadgeRepository = userBadgeRepository;
        this.profileRepository = profileRepository;
    }

    @Override
    @Transactional
    public void showcase(UUID userId, UUID badgeId) {
        UserBadge userBadge = userBadgeRepository.findByUserIdAndBadgeId(userId, badgeId)
            .orElseThrow(() -> new ResourceNotFoundException("UserBadge", "badgeId", badgeId));

        userBadge.showcase();
        userBadgeRepository.save(userBadge);

        PublicProfile profile = profileRepository.findByUserId(userId).orElse(null);
        if (profile != null) {
            profile.showcaseBadge(badgeId);
            profileRepository.save(profile);
        }
    }

    @Override
    @Transactional
    public void unshowcase(UUID userId, UUID badgeId) {
        UserBadge userBadge = userBadgeRepository.findByUserIdAndBadgeId(userId, badgeId)
            .orElseThrow(() -> new ResourceNotFoundException("UserBadge", "badgeId", badgeId));

        userBadge.unshowcase();
        userBadgeRepository.save(userBadge);

        PublicProfile profile = profileRepository.findByUserId(userId).orElse(null);
        if (profile != null) {
            profile.removeBadgeShowcase(badgeId);
            profileRepository.save(profile);
        }
    }
}
