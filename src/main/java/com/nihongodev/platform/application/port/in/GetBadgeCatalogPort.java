package com.nihongodev.platform.application.port.in;

import com.nihongodev.platform.application.dto.BadgeDto;
import com.nihongodev.platform.domain.model.BadgeCategory;

import java.util.List;

public interface GetBadgeCatalogPort {
    List<BadgeDto> getAll();
    List<BadgeDto> getByCategory(BadgeCategory category);
}
