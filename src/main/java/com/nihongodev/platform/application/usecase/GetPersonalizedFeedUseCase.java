package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.dto.ContentFeedDto;
import com.nihongodev.platform.application.dto.RealContentDto;
import com.nihongodev.platform.application.port.in.GetPersonalizedFeedPort;
import com.nihongodev.platform.application.port.out.RealContentRepositoryPort;
import com.nihongodev.platform.application.port.out.UserContentPreferenceRepositoryPort;
import com.nihongodev.platform.application.service.realcontent.selector.PersonalizedContentSelector;
import com.nihongodev.platform.domain.model.JapaneseLevel;
import com.nihongodev.platform.domain.model.RealContent;
import com.nihongodev.platform.domain.model.UserContentPreference;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class GetPersonalizedFeedUseCase implements GetPersonalizedFeedPort {

    private final RealContentRepositoryPort contentRepository;
    private final UserContentPreferenceRepositoryPort preferenceRepository;
    private final PersonalizedContentSelector selector;

    public GetPersonalizedFeedUseCase(RealContentRepositoryPort contentRepository,
                                      UserContentPreferenceRepositoryPort preferenceRepository,
                                      PersonalizedContentSelector selector) {
        this.contentRepository = contentRepository;
        this.preferenceRepository = preferenceRepository;
        this.selector = selector;
    }

    @Override
    public ContentFeedDto execute(UUID userId) {
        UserContentPreference preference = preferenceRepository.findByUserId(userId)
            .orElse(UserContentPreference.defaults(userId, JapaneseLevel.N3));

        List<RealContent> allPublished = contentRepository.findPublished();
        List<RealContent> recommended = selector.selectForUser(allPublished, preference, 10);
        List<RealContent> newArrivals = contentRepository.findRecentPublished(5);

        return new ContentFeedDto(
            recommended.stream().map(RealContentMapper::toDto).toList(),
            newArrivals.stream().map(RealContentMapper::toDto).toList(),
            allPublished.size()
        );
    }
}
