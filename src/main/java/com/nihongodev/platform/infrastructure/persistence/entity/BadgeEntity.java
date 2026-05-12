package com.nihongodev.platform.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "badges")
public class BadgeEntity {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(name = "name_jp", nullable = false)
    private String nameJp;

    @Column(name = "name_en", nullable = false)
    private String nameEn;

    @Column(name = "description_jp")
    private String descriptionJp;

    @Column(name = "description_en")
    private String descriptionEn;

    @Column(name = "icon_url")
    private String iconUrl;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private String rarity;

    @Column(name = "related_module")
    private String relatedModule;

    @Column(name = "required_score")
    private int requiredScore;

    @Column(name = "required_count")
    private int requiredCount;

    @Column(name = "xp_reward")
    private int xpReward;

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
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getRarity() { return rarity; }
    public void setRarity(String rarity) { this.rarity = rarity; }
    public String getRelatedModule() { return relatedModule; }
    public void setRelatedModule(String relatedModule) { this.relatedModule = relatedModule; }
    public int getRequiredScore() { return requiredScore; }
    public void setRequiredScore(int requiredScore) { this.requiredScore = requiredScore; }
    public int getRequiredCount() { return requiredCount; }
    public void setRequiredCount(int requiredCount) { this.requiredCount = requiredCount; }
    public int getXpReward() { return xpReward; }
    public void setXpReward(int xpReward) { this.xpReward = xpReward; }
}
