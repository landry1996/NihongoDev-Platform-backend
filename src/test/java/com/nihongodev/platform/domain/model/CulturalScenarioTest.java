package com.nihongodev.platform.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("CulturalScenario Domain Model")
class CulturalScenarioTest {

    @Test
    @DisplayName("should create scenario with all valid fields set")
    void shouldCreateWithValidFields() {
        CulturalScenario scenario = CulturalScenario.create(
                "Meeting Greeting", "会議の挨拶",
                "You arrive at a meeting with your manager", "上司との会議に到着しました",
                WorkplaceContext.MEETING, RelationshipType.TO_SUPERIOR,
                ScenarioMode.MULTIPLE_CHOICE, ScenarioCategory.COMMUNICATION,
                KeigoLevel.SONKEIGO, JapaneseLevel.N3, 50);

        assertThat(scenario.getId()).isNotNull();
        assertThat(scenario.getTitle()).isEqualTo("Meeting Greeting");
        assertThat(scenario.getTitleJp()).isEqualTo("会議の挨拶");
        assertThat(scenario.getContext()).isEqualTo(WorkplaceContext.MEETING);
        assertThat(scenario.getRelationship()).isEqualTo(RelationshipType.TO_SUPERIOR);
        assertThat(scenario.getMode()).isEqualTo(ScenarioMode.MULTIPLE_CHOICE);
        assertThat(scenario.getCategory()).isEqualTo(ScenarioCategory.COMMUNICATION);
        assertThat(scenario.getExpectedKeigoLevel()).isEqualTo(KeigoLevel.SONKEIGO);
        assertThat(scenario.getDifficulty()).isEqualTo(JapaneseLevel.N3);
        assertThat(scenario.getXpReward()).isEqualTo(50);
        assertThat(scenario.isPublished()).isFalse();
        assertThat(scenario.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("should support choices list")
    void shouldSupportChoicesList() {
        CulturalScenario scenario = CulturalScenario.create(
                "Email Reply", "メール返信", "Reply to client", "クライアントに返信",
                WorkplaceContext.EMAIL, RelationshipType.TO_CLIENT,
                ScenarioMode.MULTIPLE_CHOICE, ScenarioCategory.COMMUNICATION,
                KeigoLevel.KENJOUGO, JapaneseLevel.N2, 75);

        scenario.setChoices(List.of(
                new ScenarioChoice("Polite reply", "丁寧な返信", true, 90, "Excellent"),
                new ScenarioChoice("Casual reply", "カジュアルな返信", false, 20, "Too casual")
        ));

        assertThat(scenario.getChoices()).hasSize(2);
        assertThat(scenario.getChoices().get(0).isOptimal()).isTrue();
        assertThat(scenario.getChoices().get(0).culturalScore()).isEqualTo(90);
    }

    @Test
    @DisplayName("should support key and avoid phrases")
    void shouldSupportKeyAndAvoidPhrases() {
        CulturalScenario scenario = CulturalScenario.create(
                "Nomikai Decline", "飲み会を断る", "Declining invitation", "誘いを断る",
                WorkplaceContext.NOMIKAI, RelationshipType.TO_SUPERIOR,
                ScenarioMode.FREE_TEXT, ScenarioCategory.SOCIAL,
                KeigoLevel.TEINEIGO, JapaneseLevel.N3, 60);

        scenario.setKeyPhrases(List.of("申し訳ございません", "残念ですが"));
        scenario.setAvoidPhrases(List.of("無理", "行きたくない"));

        assertThat(scenario.getKeyPhrases()).containsExactly("申し訳ございません", "残念ですが");
        assertThat(scenario.getAvoidPhrases()).containsExactly("無理", "行きたくない");
    }

    @Test
    @DisplayName("should set xpReward correctly via factory method")
    void shouldSetXpReward() {
        CulturalScenario low = CulturalScenario.create(
                "Simple", "簡単", "Greet", "挨拶",
                WorkplaceContext.STANDUP, RelationshipType.TO_PEER,
                ScenarioMode.MULTIPLE_CHOICE, ScenarioCategory.COMMUNICATION,
                KeigoLevel.TEINEIGO, JapaneseLevel.N5, 25);

        CulturalScenario high = CulturalScenario.create(
                "Complex", "複雑", "Negotiate", "交渉",
                WorkplaceContext.CLIENT_VISIT, RelationshipType.TO_CLIENT,
                ScenarioMode.ROLE_PLAY, ScenarioCategory.NEGOTIATION,
                KeigoLevel.MIXED_FORMAL, JapaneseLevel.N1, 150);

        assertThat(low.getXpReward()).isEqualTo(25);
        assertThat(high.getXpReward()).isEqualTo(150);
    }
}
