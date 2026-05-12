package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.command.CreatePublicProfileCommand;
import com.nihongodev.platform.application.dto.PublicProfileDto;
import com.nihongodev.platform.application.port.in.CreatePublicProfilePort;
import com.nihongodev.platform.application.port.out.PublicProfileRepositoryPort;
import com.nihongodev.platform.domain.model.PublicProfile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CreatePublicProfileUseCase implements CreatePublicProfilePort {

    private final PublicProfileRepositoryPort profileRepository;

    public CreatePublicProfileUseCase(PublicProfileRepositoryPort profileRepository) {
        this.profileRepository = profileRepository;
    }

    @Override
    @Transactional
    public PublicProfileDto execute(CreatePublicProfileCommand command) {
        if (profileRepository.findByUserId(command.userId()).isPresent()) {
            throw new IllegalArgumentException("Profile already exists for this user");
        }

        String slug = command.slug();
        if (profileRepository.existsBySlug(slug)) {
            slug = slug + "-" + System.currentTimeMillis() % 10000;
        }

        PublicProfile profile = PublicProfile.create(command.userId(), command.displayName(), slug);
        profile.setBio(command.bio());
        profile.setCurrentLevel(command.currentLevel());
        profile.setVisibility(command.visibility());
        profile.setOpenToWork(command.openToWork());
        profile.setPreferredRole(command.preferredRole());
        profile.setLocation(command.location());
        profile.setHighlightedSkills(command.highlightedSkills() != null ? command.highlightedSkills() : List.of());

        profile = profileRepository.save(profile);
        return PortfolioMapper.toDto(profile, List.of());
    }
}
