package com.nihongodev.platform.application.port.in;

import com.nihongodev.platform.application.dto.RecruiterSearchResultDto;
import com.nihongodev.platform.domain.model.JapaneseLevel;

public interface SearchProfilesPort {
    RecruiterSearchResultDto search(JapaneseLevel minLevel, String skill, Boolean openToWork,
                                    int page, int pageSize);
}
