package com.nihongodev.platform.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class GeneratedPitch {

    private UUID id;
    private UUID userId;
    private PitchType pitchType;
    private String content;
    private UUID profileSnapshotId;
    private LocalDateTime generatedAt;

    public GeneratedPitch() {}

    public static GeneratedPitch create(UUID userId, PitchType pitchType, String content, UUID profileSnapshotId) {
        GeneratedPitch pitch = new GeneratedPitch();
        pitch.id = UUID.randomUUID();
        pitch.userId = userId;
        pitch.pitchType = pitchType;
        pitch.content = content;
        pitch.profileSnapshotId = profileSnapshotId;
        pitch.generatedAt = LocalDateTime.now();
        return pitch;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public PitchType getPitchType() { return pitchType; }
    public void setPitchType(PitchType pitchType) { this.pitchType = pitchType; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public UUID getProfileSnapshotId() { return profileSnapshotId; }
    public void setProfileSnapshotId(UUID profileSnapshotId) { this.profileSnapshotId = profileSnapshotId; }
    public LocalDateTime getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; }
}
