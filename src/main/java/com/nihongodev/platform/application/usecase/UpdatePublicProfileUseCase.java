package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.command.UpdatePublicProfileCommand;
import com.nihongodev.platform.application.dto.PublicProfileDto;
import com.nihongodev.platform.application.port.in.UpdatePublicProfilePort;
import com.nihongodev.platform.application.port.out.BadgeRepositoryPort;
import com.nihongodev.platform.application.port.out.PublicProfileRepositoryPort;
import com.nihongodev.platform.domain.exception.ResourceNotFoundException;
import com.nihongodev.platform.domain.model.PublicProfile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UpdatePublicProfileUseCase implements UpdatePublicProfilePort {

    private final PublicProfileRepositoryPort profileRepository;
    private final BadgeRepositoryPort badgeRepository;

    public UpdatePublicProfileUseCase(PublicProfileRepositoryPort profileRepository,
                                      BadgeRepositoryPort badgeRepository) {
        this.profileRepository = profileRepository;
        this.badgeRepository = badgeRepository;
    }

    @Override
    @Transactional
    public PublicProfileDto execute(UpdatePublicProfileCommand command) {
        PublicProfile profile = profileRepository.findByUserId(command.userId())
            .orElseThrow(() -> new ResourceNotFoundException("PublicProfile", "userId", command.userId()));

        profile.setDisplayName(command.displayName());
        profile.setBio(command.bio());
        profile.setCurrentLevel(command.currentLevel());
        profile.setVisibility(command.visibility());
        profile.setOpenToWork(command.openToWork());
        profile.setPreferredRole(command.preferredRole());
        profile.setLocation(command.location());
        profile.setHighlightedSkills(command.highlightedSkills() != null ? command.highlightedSkills() : List.of());

        profile = profileRepository.save(profile);

        var showcasedBadges = badgeRepository.findByIds(profile.getShowcasedBadgeIds()).stream()
            .map(PortfolioMapper::toBadgeDto)
            .toList();

        return PortfolioMapper.toDto(profile, showcasedBadges);
    }
}
