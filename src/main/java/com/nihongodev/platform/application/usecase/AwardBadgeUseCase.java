package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.dto.UserBadgeDto;
import com.nihongodev.platform.application.port.in.AwardBadgePort;
import com.nihongodev.platform.application.port.out.BadgeRepositoryPort;
import com.nihongodev.platform.application.port.out.EventPublisherPort;
import com.nihongodev.platform.application.port.out.UserBadgeRepositoryPort;
import com.nihongodev.platform.domain.event.BadgeEarnedEvent;
import com.nihongodev.platform.domain.exception.ResourceNotFoundException;
import com.nihongodev.platform.domain.model.Badge;
import com.nihongodev.platform.domain.model.UserBadge;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class AwardBadgeUseCase implements AwardBadgePort {

    private final BadgeRepositoryPort badgeRepository;
    private final UserBadgeRepositoryPort userBadgeRepository;
    private final EventPublisherPort eventPublisher;

    public AwardBadgeUseCase(BadgeRepositoryPort badgeRepository,
                              UserBadgeRepositoryPort userBadgeRepository,
                              EventPublisherPort eventPublisher) {
        this.badgeRepository = badgeRepository;
        this.userBadgeRepository = userBadgeRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public UserBadgeDto execute(UUID userId, String badgeCode) {
        Badge badge = badgeRepository.findByCode(badgeCode)
            .orElseThrow(() -> new ResourceNotFoundException("Badge", "code", badgeCode));

        if (userBadgeRepository.existsByUserIdAndBadgeId(userId, badge.getId())) {
            throw new IllegalArgumentException("User already has badge: " + badgeCode);
        }

        UserBadge userBadge = UserBadge.award(userId, badge.getId());
        userBadge = userBadgeRepository.save(userBadge);

        eventPublisher.publish("badge-events", BadgeEarnedEvent.create(
            userId, badge.getId(), badge.getCode(), badge.getNameJp(), badge.getXpReward()
        ));

        return PortfolioMapper.toUserBadgeDto(userBadge, badge);
    }
}
