-- V6: Correction Intelligente Module
-- Tables: correction_sessions, correction_annotations, correction_rules, weakness_patterns

-- Correction Sessions
CREATE TABLE correction_sessions (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id),
    original_text TEXT NOT NULL,
    text_type VARCHAR(50) NOT NULL,
    target_level VARCHAR(10) NOT NULL,
    grammar_score DOUBLE PRECISION DEFAULT 0,
    vocabulary_score DOUBLE PRECISION DEFAULT 0,
    politeness_score DOUBLE PRECISION DEFAULT 0,
    clarity_score DOUBLE PRECISION DEFAULT 0,
    naturalness_score DOUBLE PRECISION DEFAULT 0,
    professional_score DOUBLE PRECISION DEFAULT 0,
    overall_score DOUBLE PRECISION DEFAULT 0,
    total_annotations INTEGER DEFAULT 0,
    error_count INTEGER DEFAULT 0,
    warning_count INTEGER DEFAULT 0,
    info_count INTEGER DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_correction_sessions_user_id ON correction_sessions(user_id);
CREATE INDEX idx_correction_sessions_created_at ON correction_sessions(created_at DESC);
CREATE INDEX idx_correction_sessions_text_type ON correction_sessions(text_type);

-- Correction Annotations
CREATE TABLE correction_annotations (
    id UUID PRIMARY KEY,
    session_id UUID NOT NULL REFERENCES correction_sessions(id) ON DELETE CASCADE,
    start_offset INTEGER NOT NULL,
    end_offset INTEGER NOT NULL,
    severity VARCHAR(20) NOT NULL,
    category VARCHAR(30) NOT NULL,
    original_text TEXT,
    suggestion TEXT,
    explanation TEXT,
    rule_id VARCHAR(255)
);

CREATE INDEX idx_correction_annotations_session_id ON correction_annotations(session_id);
CREATE INDEX idx_correction_annotations_category ON correction_annotations(category);
CREATE INDEX idx_correction_annotations_severity ON correction_annotations(severity);

-- Correction Rules (extensible rule engine)
CREATE TABLE correction_rules (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    category VARCHAR(30) NOT NULL,
    severity VARCHAR(20) NOT NULL,
    pattern VARCHAR(500) NOT NULL,
    suggestion_template TEXT,
    explanation_template TEXT,
    applicable_contexts VARCHAR(500),
    min_level VARCHAR(10),
    active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE INDEX idx_correction_rules_active ON correction_rules(active);
CREATE INDEX idx_correction_rules_category ON correction_rules(category);

-- Weakness Patterns (recurring error tracking)
CREATE TABLE weakness_patterns (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id),
    category VARCHAR(30) NOT NULL,
    pattern_description VARCHAR(500) NOT NULL,
    occurrence_count INTEGER NOT NULL DEFAULT 1,
    last_example TEXT
);

CREATE INDEX idx_weakness_patterns_user_id ON weakness_patterns(user_id);
CREATE INDEX idx_weakness_patterns_category ON weakness_patterns(category);
CREATE UNIQUE INDEX idx_weakness_patterns_unique ON weakness_patterns(user_id, category, pattern_description);

-- Seed correction rules
INSERT INTO correction_rules (id, name, category, severity, pattern, suggestion_template, explanation_template, applicable_contexts, min_level, active) VALUES
(gen_random_uuid(), 'Double particle を', 'GRAMMAR', 'ERROR', 'を.*を', 'Remove one of the を particles', 'Japanese sentences typically have only one を particle per clause', NULL, 'N5', true),
(gen_random_uuid(), 'Missing です/ます in formal', 'KEIGO', 'WARNING', '[^です|ます|ました|ません]$', 'End with です/ます form', 'Formal writing requires です/ます endings', 'EMAIL_TO_CLIENT||INTERVIEW_ANSWER', 'N4', true),
(gen_random_uuid(), 'Casual だ in formal context', 'KEIGO', 'WARNING', 'だ[。、]', 'Replace だ with です', 'Use です instead of だ in formal contexts', 'EMAIL_TO_CLIENT||STANDUP_REPORT', 'N5', true),
(gen_random_uuid(), 'Redundant という', 'NATURALNESS', 'INFO', 'というのは.*ということ', 'Simplify the sentence', 'Using both というのは and ということ is redundant', NULL, 'N3', true),
(gen_random_uuid(), 'Missing subject marker', 'GRAMMAR', 'INFO', '^[^はがも]{20,}', 'Consider adding は or が to clarify the subject', 'Long sentences without a topic/subject marker can be ambiguous', NULL, 'N4', true),
(gen_random_uuid(), 'Overuse of します', 'VOCABULARY', 'INFO', 'します.*します.*します', 'Vary your verb choices', 'Using します repeatedly sounds monotonous — use specific verbs', 'STANDUP_REPORT||EMAIL_TO_CLIENT', 'N3', true),
(gen_random_uuid(), 'Mixed politeness levels', 'KEIGO', 'ERROR', '(ございます|いただ).*(だよ|じゃん|だろ)', 'Use consistent politeness level', 'Mixing keigo with casual speech is inappropriate', NULL, 'N3', true),
(gen_random_uuid(), 'IT term without context', 'PROFESSIONAL', 'INFO', '(デプロイ|マージ|プルリク)', 'Consider adding Japanese explanation for non-IT readers', 'IT English loanwords may need clarification in formal Japanese communications', 'EMAIL_TO_CLIENT', 'N3', true);
