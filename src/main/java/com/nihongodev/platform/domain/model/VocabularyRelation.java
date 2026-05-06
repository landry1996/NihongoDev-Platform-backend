package com.nihongodev.platform.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class VocabularyRelation {

    private UUID id;
    private UUID sourceVocabularyId;
    private UUID targetVocabularyId;
    private RelationType relationType;
    private LocalDateTime createdAt;

    public VocabularyRelation() {}

    public static VocabularyRelation create(UUID sourceId, UUID targetId, RelationType type) {
        VocabularyRelation r = new VocabularyRelation();
        r.id = UUID.randomUUID();
        r.sourceVocabularyId = sourceId;
        r.targetVocabularyId = targetId;
        r.relationType = type;
        r.createdAt = LocalDateTime.now();
        return r;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getSourceVocabularyId() { return sourceVocabularyId; }
    public void setSourceVocabularyId(UUID sourceVocabularyId) { this.sourceVocabularyId = sourceVocabularyId; }
    public UUID getTargetVocabularyId() { return targetVocabularyId; }
    public void setTargetVocabularyId(UUID targetVocabularyId) { this.targetVocabularyId = targetVocabularyId; }
    public RelationType getRelationType() { return relationType; }
    public void setRelationType(RelationType relationType) { this.relationType = relationType; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
