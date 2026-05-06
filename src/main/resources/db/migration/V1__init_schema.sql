CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL DEFAULT 'LEARNER',
    japanese_level VARCHAR(50) DEFAULT 'BEGINNER',
    objective VARCHAR(100),
    avatar_url VARCHAR(500),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE lessons (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    title VARCHAR(200) NOT NULL,
    description TEXT,
    type VARCHAR(50) NOT NULL,
    level VARCHAR(50) NOT NULL,
    content TEXT,
    order_index INT DEFAULT 0,
    is_published BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE vocabularies (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    lesson_id UUID REFERENCES lessons(id) ON DELETE SET NULL,
    french VARCHAR(255),
    english VARCHAR(255),
    japanese VARCHAR(255) NOT NULL,
    romaji VARCHAR(255),
    example TEXT,
    category VARCHAR(100) NOT NULL,
    domain VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE quizzes (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    lesson_id UUID REFERENCES lessons(id) ON DELETE CASCADE,
    title VARCHAR(200) NOT NULL,
    level VARCHAR(50) NOT NULL,
    time_limit_seconds INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE questions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    quiz_id UUID REFERENCES quizzes(id) ON DELETE CASCADE,
    content TEXT NOT NULL,
    correct_answer TEXT NOT NULL,
    explanation TEXT,
    question_type VARCHAR(50) DEFAULT 'MULTIPLE_CHOICE',
    options JSONB,
    order_index INT DEFAULT 0
);

CREATE TABLE interview_sessions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID REFERENCES users(id) ON DELETE CASCADE,
    type VARCHAR(100) NOT NULL,
    difficulty VARCHAR(50) DEFAULT 'INTERMEDIATE',
    started_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP,
    global_score INT,
    feedback TEXT
);

CREATE TABLE answers (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    session_id UUID REFERENCES interview_sessions(id) ON DELETE CASCADE,
    question_content TEXT NOT NULL,
    user_response TEXT,
    corrected_response TEXT,
    grammar_score INT,
    vocabulary_score INT,
    professional_score INT,
    politeness_score INT,
    global_score INT,
    feedback TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE progress (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    completed_lessons INT DEFAULT 0,
    completed_quizzes INT DEFAULT 0,
    completed_interviews INT DEFAULT 0,
    average_score DOUBLE PRECISION DEFAULT 0,
    global_progress DOUBLE PRECISION DEFAULT 0,
    current_streak INT DEFAULT 0,
    longest_streak INT DEFAULT 0,
    total_study_time_minutes INT DEFAULT 0,
    last_activity_at TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE refresh_tokens (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID REFERENCES users(id) ON DELETE CASCADE,
    token VARCHAR(500) UNIQUE NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_vocabularies_category ON vocabularies(category);
CREATE INDEX idx_vocabularies_domain ON vocabularies(domain);
CREATE INDEX idx_interview_sessions_user ON interview_sessions(user_id);
CREATE INDEX idx_answers_session ON answers(session_id);
CREATE INDEX idx_refresh_tokens_token ON refresh_tokens(token);
CREATE INDEX idx_refresh_tokens_user ON refresh_tokens(user_id);
