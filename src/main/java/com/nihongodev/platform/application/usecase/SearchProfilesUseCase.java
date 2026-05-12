package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.dto.RecruiterSearchResultDto;
import com.nihongodev.platform.application.port.in.SearchProfilesPort;
import com.nihongodev.platform.application.port.out.BadgeRepositoryPort;
import com.nihongodev.platform.application.port.out.PublicProfileRepositoryPort;
import com.nihongodev.platform.domain.model.JapaneseLevel;
import com.nihongodev.platform.domain.model.PublicProfile;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchProfilesUseCase implements SearchProfilesPort {

    private final PublicProfileRepositoryPort profileRepository;
    private final BadgeRepositoryPort badgeRepository;

    public SearchProfilesUseCase(PublicProfileRepositoryPort profileRepository,
                                  BadgeRepositoryPort badgeRepository) {
        this.profileRepository = profileRepository;
        this.badgeRepository = badgeRepository;
    }

    @Override
    public RecruiterSearchResultDto search(JapaneseLevel minLevel, String skill,
                                            Boolean openToWork, int page, int pageSize) {
        int offset = page * pageSize;
        List<PublicProfile> profiles = profileRepository.searchProfiles(
            minLevel, skill, openToWork, offset, pageSize
        );
        int total = profileRepository.countSearchResults(minLevel, skill, openToWork);

        var profileDtos = profiles.stream().map(profile -> {
            var showcasedBadges = badgeRepository.findByIds(profile.getShowcasedBadgeIds()).stream()
                .map(PortfolioMapper::toBadgeDto)
                .toList();
            return PortfolioMapper.toDto(profile, showcasedBadges);
        }).toList();

        return new RecruiterSearchResultDto(profileDtos, total, page, pageSize);
    }
}
