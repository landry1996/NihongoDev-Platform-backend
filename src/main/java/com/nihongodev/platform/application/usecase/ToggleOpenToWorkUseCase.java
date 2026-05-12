package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.dto.PublicProfileDto;
import com.nihongodev.platform.application.port.in.ToggleOpenToWorkPort;
import com.nihongodev.platform.application.port.out.BadgeRepositoryPort;
import com.nihongodev.platform.application.port.out.PublicProfileRepositoryPort;
import com.nihongodev.platform.domain.exception.ResourceNotFoundException;
import com.nihongodev.platform.domain.model.PublicProfile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ToggleOpenToWorkUseCase implements ToggleOpenToWorkPort {

    private final PublicProfileRepositoryPort profileRepository;
    private final BadgeRepositoryPort badgeRepository;

    public ToggleOpenToWorkUseCase(PublicProfileRepositoryPort profileRepository,
                                    BadgeRepositoryPort badgeRepository) {
        this.profileRepository = profileRepository;
        this.badgeRepository = badgeRepository;
    }

    @Override
    @Transactional
    public PublicProfileDto execute(UUID userId) {
        PublicProfile profile = profileRepository.findByUserId(userId)
            .orElseThrow(() -> new ResourceNotFoundException("PublicProfile", "userId", userId));

        profile.toggleOpenToWork();
        profile = profileRepository.save(profile);

        var showcasedBadges = badgeRepository.findByIds(profile.getShowcasedBadgeIds()).stream()
            .map(PortfolioMapper::toBadgeDto)
            .toList();

        return PortfolioMapper.toDto(profile, showcasedBadges);
    }
}
