package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.dto.UserBadgeDto;
import com.nihongodev.platform.application.port.in.GetUserBadgesPort;
import com.nihongodev.platform.application.port.out.BadgeRepositoryPort;
import com.nihongodev.platform.application.port.out.UserBadgeRepositoryPort;
import com.nihongodev.platform.domain.model.Badge;
import com.nihongodev.platform.domain.model.UserBadge;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class GetUserBadgesUseCase implements GetUserBadgesPort {

    private final UserBadgeRepositoryPort userBadgeRepository;
    private final BadgeRepositoryPort badgeRepository;

    public GetUserBadgesUseCase(UserBadgeRepositoryPort userBadgeRepository,
                                 BadgeRepositoryPort badgeRepository) {
        this.userBadgeRepository = userBadgeRepository;
        this.badgeRepository = badgeRepository;
    }

    @Override
    public List<UserBadgeDto> execute(UUID userId) {
        List<UserBadge> userBadges = userBadgeRepository.findByUserId(userId);
        return userBadges.stream().map(ub -> {
            Badge badge = badgeRepository.findById(ub.getBadgeId()).orElse(null);
            return badge != null ? PortfolioMapper.toUserBadgeDto(ub, badge) : null;
        }).filter(dto -> dto != null).toList();
    }
}
