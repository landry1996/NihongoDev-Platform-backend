-- V7: Progress & Analytics Module
-- Tables: user_progress, module_progress, learning_activities, user_statistics

-- User Progress (global progress aggregate)
CREATE TABLE user_progress (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL UNIQUE REFERENCES users(id),
    total_lessons_completed INT NOT NULL DEFAULT 0,
    total_quizzes_completed INT NOT NULL DEFAULT 0,
    total_interviews_completed INT NOT NULL DEFAULT 0,
    total_corrections_completed INT NOT NULL DEFAULT 0,
    global_score DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    current_streak INT NOT NULL DEFAULT 0,
    longest_streak INT NOT NULL DEFAULT 0,
    last_activity_at TIMESTAMP,
    level VARCHAR(20) NOT NULL DEFAULT 'BEGINNER',
    total_xp BIGINT NOT NULL DEFAULT 0,
    scored_activities_count INT NOT NULL DEFAULT 0,
    weighted_score_sum DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    weight_sum DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_user_progress_level ON user_progress(level);
CREATE INDEX idx_user_progress_xp ON user_progress(total_xp DESC);

-- Module Progress (per-module tracking)
CREATE TABLE module_progress (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id),
    module_type VARCHAR(30) NOT NULL,
    completed_items INT NOT NULL DEFAULT 0,
    total_items INT,
    average_score DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    best_score DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    last_completed_at TIMESTAMP,
    status VARCHAR(20) NOT NULL DEFAULT 'NOT_STARTED',
    UNIQUE(user_id, module_type)
);

CREATE INDEX idx_module_progress_user ON module_progress(user_id);

-- Learning Activities (immutable activity log)
CREATE TABLE learning_activities (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id),
    activity_type VARCHAR(30) NOT NULL,
    reference_id UUID NOT NULL,
    score DOUBLE PRECISION,
    xp_earned INT NOT NULL DEFAULT 0,
    metadata JSONB,
    occurred_at TIMESTAMP NOT NULL,
    UNIQUE(user_id, reference_id, activity_type)
);

CREATE INDEX idx_learning_activities_user_date ON learning_activities(user_id, occurred_at DESC);
CREATE INDEX idx_learning_activities_reference ON learning_activities(user_id, reference_id, activity_type);
CREATE INDEX idx_learning_activities_type ON learning_activities(activity_type);

-- User Statistics (read-side projection, batch-updated)
CREATE TABLE user_statistics (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL UNIQUE REFERENCES users(id),
    average_score_7_days DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    average_score_30_days DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    average_score_all_time DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    learning_velocity DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    consistency_rate DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    weak_areas JSONB DEFAULT '[]',
    recommendations JSONB DEFAULT '[]',
    progress_trend VARCHAR(20) NOT NULL DEFAULT 'STABLE',
    last_calculated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_user_statistics_user ON user_statistics(user_id);
