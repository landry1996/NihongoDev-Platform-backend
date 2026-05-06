package com.nihongodev.platform.infrastructure.persistence.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "vocabulary_relations",
        uniqueConstraints = @UniqueConstraint(columnNames = {"source_vocabulary_id", "target_vocabulary_id", "relation_type"}))
public class VocabularyRelationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "source_vocabulary_id", nullable = false)
    private UUID sourceVocabularyId;

    @Column(name = "target_vocabulary_id", nullable = false)
    private UUID targetVocabularyId;

    @Column(name = "relation_type", nullable = false, length = 50)
    private String relationType;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getSourceVocabularyId() { return sourceVocabularyId; }
    public void setSourceVocabularyId(UUID sourceVocabularyId) { this.sourceVocabularyId = sourceVocabularyId; }
    public UUID getTargetVocabularyId() { return targetVocabularyId; }
    public void setTargetVocabularyId(UUID targetVocabularyId) { this.targetVocabularyId = targetVocabularyId; }
    public String getRelationType() { return relationType; }
    public void setRelationType(String relationType) { this.relationType = relationType; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
