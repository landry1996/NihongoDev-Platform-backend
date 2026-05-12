package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.dto.PublicProfileDto;
import com.nihongodev.platform.application.port.in.GetPublicProfilePort;
import com.nihongodev.platform.application.port.out.BadgeRepositoryPort;
import com.nihongodev.platform.application.port.out.PublicProfileRepositoryPort;
import com.nihongodev.platform.domain.exception.ResourceNotFoundException;
import com.nihongodev.platform.domain.model.PublicProfile;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class GetPublicProfileUseCase implements GetPublicProfilePort {

    private final PublicProfileRepositoryPort profileRepository;
    private final BadgeRepositoryPort badgeRepository;

    public GetPublicProfileUseCase(PublicProfileRepositoryPort profileRepository,
                                    BadgeRepositoryPort badgeRepository) {
        this.profileRepository = profileRepository;
        this.badgeRepository = badgeRepository;
    }

    @Override
    public PublicProfileDto getBySlug(String slug, boolean isRecruiter) {
        PublicProfile profile = profileRepository.findBySlug(slug)
            .orElseThrow(() -> new ResourceNotFoundException("PublicProfile", "slug", slug));

        if (!profile.isVisibleTo(isRecruiter)) {
            throw new ResourceNotFoundException("PublicProfile", "slug", slug);
        }

        var showcasedBadges = badgeRepository.findByIds(profile.getShowcasedBadgeIds()).stream()
            .map(PortfolioMapper::toBadgeDto)
            .toList();

        return PortfolioMapper.toDto(profile, showcasedBadges);
    }

    @Override
    public PublicProfileDto getByUserId(UUID userId) {
        PublicProfile profile = profileRepository.findByUserId(userId)
            .orElseThrow(() -> new ResourceNotFoundException("PublicProfile", "userId", userId));

        var showcasedBadges = badgeRepository.findByIds(profile.getShowcasedBadgeIds()).stream()
            .map(PortfolioMapper::toBadgeDto)
            .toList();

        return PortfolioMapper.toDto(profile, showcasedBadges);
    }
}
