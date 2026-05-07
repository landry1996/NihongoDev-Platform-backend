-- V8: CV Generator Module
-- Tables: cv_profiles, generated_pitches

CREATE TABLE cv_profiles (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL UNIQUE REFERENCES users(id),
    full_name VARCHAR(200) NOT NULL,
    current_role VARCHAR(200),
    target_role VARCHAR(200) NOT NULL,
    years_of_experience INT NOT NULL DEFAULT 0,
    target_company_type VARCHAR(50) NOT NULL,
    tech_stack JSONB NOT NULL DEFAULT '[]',
    experiences JSONB NOT NULL DEFAULT '[]',
    certifications JSONB NOT NULL DEFAULT '[]',
    notable_projects JSONB NOT NULL DEFAULT '[]',
    motivation_japan TEXT,
    japanese_level VARCHAR(10),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE generated_pitches (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id),
    pitch_type VARCHAR(50) NOT NULL,
    content TEXT NOT NULL,
    profile_snapshot_id UUID REFERENCES cv_profiles(id),
    generated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_generated_pitches_user_type ON generated_pitches(user_id, pitch_type);
CREATE INDEX idx_generated_pitches_user_date ON generated_pitches(user_id, generated_at DESC);
