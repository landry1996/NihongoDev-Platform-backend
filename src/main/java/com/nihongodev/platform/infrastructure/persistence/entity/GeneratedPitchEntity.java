package com.nihongodev.platform.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "generated_pitches")
public class GeneratedPitchEntity {

    @Id
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "pitch_type", nullable = false, length = 50)
    private String pitchType;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "profile_snapshot_id")
    private UUID profileSnapshotId;

    @Column(name = "generated_at", nullable = false)
    private LocalDateTime generatedAt;

    @PrePersist
    void prePersist() {
        if (generatedAt == null) generatedAt = LocalDateTime.now();
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public String getPitchType() { return pitchType; }
    public void setPitchType(String pitchType) { this.pitchType = pitchType; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public UUID getProfileSnapshotId() { return profileSnapshotId; }
    public void setProfileSnapshotId(UUID profileSnapshotId) { this.profileSnapshotId = profileSnapshotId; }
    public LocalDateTime getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; }
}
