package com.nihongodev.platform.domain.model;

import java.util.UUID;

public class Badge {

    private UUID id;
    private String code;
    private String nameJp;
    private String nameEn;
    private String descriptionJp;
    private String descriptionEn;
    private String iconUrl;
    private BadgeCategory category;
    private BadgeRarity rarity;
    private ModuleType relatedModule;
    private int requiredScore;
    private int requiredCount;
    private int xpReward;

    public static Badge define(String code, String nameJp, String nameEn,
                               BadgeCategory category, BadgeRarity rarity,
                               int requiredScore, int requiredCount, int xpReward) {
        Badge badge = new Badge();
        badge.id = UUID.randomUUID();
        badge.code = code;
        badge.nameJp = nameJp;
        badge.nameEn = nameEn;
        badge.category = category;
        badge.rarity = rarity;
        badge.requiredScore = requiredScore;
        badge.requiredCount = requiredCount;
        badge.xpReward = xpReward;
        return badge;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getNameJp() { return nameJp; }
    public void setNameJp(String nameJp) { this.nameJp = nameJp; }
    public String getNameEn() { return nameEn; }
    public void setNameEn(String nameEn) { this.nameEn = nameEn; }
    public String getDescriptionJp() { return descriptionJp; }
    public void setDescriptionJp(String descriptionJp) { this.descriptionJp = descriptionJp; }
    public String getDescriptionEn() { return descriptionEn; }
    public void setDescriptionEn(String descriptionEn) { this.descriptionEn = descriptionEn; }
    public String getIconUrl() { return iconUrl; }
    public void setIconUrl(String iconUrl) { this.iconUrl = iconUrl; }
    public BadgeCategory getCategory() { return category; }
    public void setCategory(BadgeCategory category) { this.category = category; }
    public BadgeRarity getRarity() { return rarity; }
    public void setRarity(BadgeRarity rarity) { this.rarity = rarity; }
    public ModuleType getRelatedModule() { return relatedModule; }
    public void setRelatedModule(ModuleType relatedModule) { this.relatedModule = relatedModule; }
    public int getRequiredScore() { return requiredScore; }
    public void setRequiredScore(int requiredScore) { this.requiredScore = requiredScore; }
    public int getRequiredCount() { return requiredCount; }
    public void setRequiredCount(int requiredCount) { this.requiredCount = requiredCount; }
    public int getXpReward() { return xpReward; }
    public void setXpReward(int xpReward) { this.xpReward = xpReward; }
}
