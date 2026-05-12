package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.dto.BadgeDto;
import com.nihongodev.platform.application.port.in.GetBadgeCatalogPort;
import com.nihongodev.platform.application.port.out.BadgeRepositoryPort;
import com.nihongodev.platform.domain.model.BadgeCategory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetBadgeCatalogUseCase implements GetBadgeCatalogPort {

    private final BadgeRepositoryPort badgeRepository;

    public GetBadgeCatalogUseCase(BadgeRepositoryPort badgeRepository) {
        this.badgeRepository = badgeRepository;
    }

    @Override
    public List<BadgeDto> getAll() {
        return badgeRepository.findAll().stream().map(PortfolioMapper::toBadgeDto).toList();
    }

    @Override
    public List<BadgeDto> getByCategory(BadgeCategory category) {
        return badgeRepository.findByCategory(category).stream().map(PortfolioMapper::toBadgeDto).toList();
    }
}
