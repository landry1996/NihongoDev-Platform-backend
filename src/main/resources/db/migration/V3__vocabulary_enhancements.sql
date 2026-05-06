-- V3: Vocabulary enhancements — SRS, word relations, tags, difficulty

ALTER TABLE vocabularies ADD COLUMN level VARCHAR(50) DEFAULT 'BEGINNER';
ALTER TABLE vocabularies ADD COLUMN tags TEXT;
ALTER TABLE vocabularies ADD COLUMN difficulty_score DOUBLE PRECISION DEFAULT 0.5;
ALTER TABLE vocabularies ADD COLUMN code_example TEXT;
ALTER TABLE vocabularies ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

-- Vocabulary Mastery (Spaced Repetition System per user)
CREATE TABLE vocabulary_mastery (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    vocabulary_id UUID NOT NULL REFERENCES vocabularies(id) ON DELETE CASCADE,
    mastery_level VARCHAR(50) NOT NULL DEFAULT 'NEW',
    ease_factor DOUBLE PRECISION NOT NULL DEFAULT 2.5,
    interval_days INT NOT NULL DEFAULT 0,
    repetitions INT NOT NULL DEFAULT 0,
    next_review_at TIMESTAMP,
    last_reviewed_at TIMESTAMP,
    correct_count INT DEFAULT 0,
    incorrect_count INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, vocabulary_id)
);

-- Vocabulary Relations (Word Graph)
CREATE TABLE vocabulary_relations (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    source_vocabulary_id UUID NOT NULL REFERENCES vocabularies(id) ON DELETE CASCADE,
    target_vocabulary_id UUID NOT NULL REFERENCES vocabularies(id) ON DELETE CASCADE,
    relation_type VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(source_vocabulary_id, target_vocabulary_id, relation_type)
);

CREATE INDEX idx_vocabulary_mastery_user ON vocabulary_mastery(user_id);
CREATE INDEX idx_vocabulary_mastery_next_review ON vocabulary_mastery(user_id, next_review_at);
CREATE INDEX idx_vocabulary_mastery_level ON vocabulary_mastery(mastery_level);
CREATE INDEX idx_vocabulary_relations_source ON vocabulary_relations(source_vocabulary_id);
CREATE INDEX idx_vocabulary_relations_target ON vocabulary_relations(target_vocabulary_id);
CREATE INDEX idx_vocabularies_level ON vocabularies(level);
CREATE INDEX idx_vocabularies_lesson ON vocabularies(lesson_id);
