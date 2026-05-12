package com.nihongodev.platform.application.service.realcontent.selector;

import com.nihongodev.platform.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("PersonalizedContentSelector")
class PersonalizedContentSelectorTest {

    private PersonalizedContentSelector selector;

    @BeforeEach
    void setUp() {
        selector = new PersonalizedContentSelector(List.of(
            new DomainRelevanceScorer(),
            new DifficultyMatchScorer(),
            new ReadingTimeScorer(),
            new SourcePreferenceScorer()
        ));
    }

    @Test
    @DisplayName("should rank matching content higher")
    void shouldRankMatchingContentHigher() {
        UserContentPreference preference = new UserContentPreference(
            UUID.randomUUID(),
            List.of(ContentDomain.WEB_DEVELOPMENT),
            JapaneseLevel.N3,
            ReadingDifficulty.INTERMEDIATE,
            10,
            List.of(ContentSource.TECH_BLOG)
        );

        RealContent matching = createContent(ContentDomain.WEB_DEVELOPMENT, ReadingDifficulty.INTERMEDIATE, ContentSource.TECH_BLOG, 10);
        RealContent nonMatching = createContent(ContentDomain.SECURITY, ReadingDifficulty.NATIVE_LEVEL, ContentSource.CONFERENCE_TALK, 60);

        List<RealContent> result = selector.selectForUser(List.of(nonMatching, matching), preference, 2);

        assertThat(result.get(0).getDomain()).isEqualTo(ContentDomain.WEB_DEVELOPMENT);
    }

    @Test
    @DisplayName("should respect limit")
    void shouldRespectLimit() {
        UserContentPreference preference = UserContentPreference.defaults(UUID.randomUUID(), JapaneseLevel.N3);

        List<RealContent> candidates = List.of(
            createContent(ContentDomain.WEB_DEVELOPMENT, ReadingDifficulty.INTERMEDIATE, ContentSource.TECH_BLOG, 10),
            createContent(ContentDomain.CLOUD_INFRASTRUCTURE, ReadingDifficulty.ADVANCED, ContentSource.API_DOCUMENTATION, 15),
            createContent(ContentDomain.SECURITY, ReadingDifficulty.BEGINNER, ContentSource.GITHUB_ISSUE, 5)
        );

        List<RealContent> result = selector.selectForUser(candidates, preference, 2);

        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("should compute score between 0 and 1")
    void shouldComputeScoreInRange() {
        UserContentPreference preference = UserContentPreference.defaults(UUID.randomUUID(), JapaneseLevel.N3);
        RealContent content = createContent(ContentDomain.WEB_DEVELOPMENT, ReadingDifficulty.INTERMEDIATE, ContentSource.TECH_BLOG, 10);

        double score = selector.computeScore(content, preference);

        assertThat(score).isBetween(0.0, 1.0);
    }

    private RealContent createContent(ContentDomain domain, ReadingDifficulty difficulty,
                                       ContentSource source, int readingMinutes) {
        RealContent content = RealContent.ingest(
            "Test", "テスト内容", "https://test.com/" + domain.name(),
            source, domain
        );
        content.setReadingDifficulty(difficulty);
        content.setEstimatedReadingMinutes(readingMinutes);
        return content;
    }
}
