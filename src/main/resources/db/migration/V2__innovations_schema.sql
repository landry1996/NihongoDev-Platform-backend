-- ============================================
-- V2: Innovation modules schema
-- Shadow Day, Code in Japanese, Cultural Intelligence,
-- Real Content Engine, Portfolio & Badges
-- ============================================

-- ============================================
-- INNOVATION 1: Shadow Day
-- ============================================

CREATE TABLE shadow_day_sessions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    company_type VARCHAR(50) NOT NULL,
    difficulty_level VARCHAR(50) NOT NULL DEFAULT 'STARTER',
    started_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP,
    time_score INT,
    accuracy_score INT,
    culture_score INT,
    professional_score INT,
    global_score INT,
    debrief_content TEXT
);

CREATE TABLE shadow_day_interactions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    session_id UUID NOT NULL REFERENCES shadow_day_sessions(id) ON DELETE CASCADE,
    time_slot VARCHAR(20) NOT NULL,
    interaction_type VARCHAR(50) NOT NULL,
    npc_message TEXT,
    user_response TEXT,
    expected_register VARCHAR(50),
    actual_register VARCHAR(50),
    score INT,
    feedback TEXT,
    cultural_notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_shadow_day_sessions_user ON shadow_day_sessions(user_id);
CREATE INDEX idx_shadow_day_interactions_session ON shadow_day_interactions(session_id);

-- ============================================
-- INNOVATION 2: Code in Japanese
-- ============================================

CREATE TABLE code_review_exercises (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    title VARCHAR(200) NOT NULL,
    language VARCHAR(50) NOT NULL,
    difficulty VARCHAR(50) NOT NULL DEFAULT 'INTERMEDIATE',
    code_content TEXT NOT NULL,
    japanese_comments TEXT NOT NULL,
    expected_understanding TEXT,
    exercise_type VARCHAR(50) NOT NULL,
    category VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE code_review_submissions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    exercise_id UUID NOT NULL REFERENCES code_review_exercises(id) ON DELETE CASCADE,
    user_answer TEXT,
    comprehension_score INT,
    writing_score INT,
    feedback TEXT,
    submitted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE pr_writing_exercises (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    scenario_description TEXT NOT NULL,
    code_diff TEXT,
    expected_pr_structure TEXT,
    difficulty VARCHAR(50) NOT NULL DEFAULT 'INTERMEDIATE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE pr_submissions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    exercise_id UUID NOT NULL REFERENCES pr_writing_exercises(id) ON DELETE CASCADE,
    pr_title TEXT,
    pr_body TEXT,
    quality_score INT,
    language_score INT,
    feedback TEXT,
    submitted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_code_review_submissions_user ON code_review_submissions(user_id);
CREATE INDEX idx_pr_submissions_user ON pr_submissions(user_id);

-- ============================================
-- INNOVATION 3: Cultural Intelligence
-- ============================================

CREATE TABLE cultural_scenarios (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    situation_description TEXT NOT NULL,
    context_type VARCHAR(50) NOT NULL,
    relationship_type VARCHAR(50) NOT NULL,
    expected_register VARCHAR(50) NOT NULL,
    difficulty VARCHAR(50) NOT NULL DEFAULT 'INTERMEDIATE',
    bad_example TEXT,
    bad_example_explanation TEXT,
    good_example TEXT,
    good_example_explanation TEXT,
    cultural_note TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE cultural_assessments (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    scenario_id UUID NOT NULL REFERENCES cultural_scenarios(id) ON DELETE CASCADE,
    user_response TEXT,
    register_score INT,
    hierarchy_score INT,
    indirectness_score INT,
    context_score INT,
    feedback TEXT,
    assessed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE user_cultural_profiles (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID UNIQUE NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    register_accuracy INT DEFAULT 0,
    hierarchy_awareness INT DEFAULT 0,
    indirectness_level INT DEFAULT 0,
    context_switching INT DEFAULT 0,
    recovery_skill INT DEFAULT 0,
    global_culture_score INT DEFAULT 0,
    weak_areas TEXT,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_cultural_assessments_user ON cultural_assessments(user_id);

-- ============================================
-- INNOVATION 4: Real Content Engine
-- ============================================

CREATE TABLE real_content_articles (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    source VARCHAR(100) NOT NULL,
    source_url TEXT,
    title TEXT NOT NULL,
    content TEXT NOT NULL,
    content_annotated TEXT,
    estimated_jlpt_level VARCHAR(10),
    technical_domain VARCHAR(100),
    topics TEXT[],
    kanji_list TEXT[],
    vocabulary_list TEXT[],
    word_count INT,
    reading_time_minutes INT,
    fetched_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE
);

CREATE TABLE content_reading_sessions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    article_id UUID NOT NULL REFERENCES real_content_articles(id) ON DELETE CASCADE,
    assistance_level INT NOT NULL DEFAULT 1,
    started_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP,
    comprehension_score INT,
    words_learned INT DEFAULT 0,
    time_spent_seconds INT DEFAULT 0
);

CREATE TABLE content_exercises (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    article_id UUID NOT NULL REFERENCES real_content_articles(id) ON DELETE CASCADE,
    exercise_type VARCHAR(50) NOT NULL,
    question TEXT NOT NULL,
    expected_answer TEXT,
    difficulty VARCHAR(20) DEFAULT 'INTERMEDIATE'
);

CREATE TABLE content_exercise_submissions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    exercise_id UUID NOT NULL REFERENCES content_exercises(id) ON DELETE CASCADE,
    user_answer TEXT,
    score INT,
    feedback TEXT,
    submitted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_real_content_domain ON real_content_articles(technical_domain);
CREATE INDEX idx_real_content_level ON real_content_articles(estimated_jlpt_level);
CREATE INDEX idx_content_reading_user ON content_reading_sessions(user_id);

-- ============================================
-- INNOVATION 5: Portfolio & Badges
-- ============================================

CREATE TABLE public_profiles (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID UNIQUE NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    username VARCHAR(100) UNIQUE,
    display_name VARCHAR(200),
    bio TEXT,
    is_public BOOLEAN DEFAULT FALSE,
    communication_level VARCHAR(50),
    technical_japanese_level VARCHAR(50),
    cultural_score INT DEFAULT 0,
    code_review_level VARCHAR(50),
    interview_readiness INT DEFAULT 0,
    last_activity_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE badges (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    code VARCHAR(100) UNIQUE NOT NULL,
    name_japanese VARCHAR(200) NOT NULL,
    name_english VARCHAR(200) NOT NULL,
    description TEXT,
    criteria TEXT,
    icon_url VARCHAR(500),
    category VARCHAR(50)
);

CREATE TABLE user_badges (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    badge_id UUID NOT NULL REFERENCES badges(id) ON DELETE CASCADE,
    earned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    evidence_summary TEXT,
    is_verified BOOLEAN DEFAULT TRUE,
    UNIQUE(user_id, badge_id)
);

CREATE TABLE recruiter_views (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    recruiter_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    profile_id UUID NOT NULL REFERENCES public_profiles(id) ON DELETE CASCADE,
    viewed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    notes TEXT
);

CREATE TABLE portfolio_shares (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    share_token VARCHAR(255) UNIQUE NOT NULL,
    recipient_email VARCHAR(200),
    expires_at TIMESTAMP,
    accessed_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_public_profiles_username ON public_profiles(username);
CREATE INDEX idx_user_badges_user ON user_badges(user_id);
CREATE INDEX idx_portfolio_shares_token ON portfolio_shares(share_token);
CREATE INDEX idx_recruiter_views_recruiter ON recruiter_views(recruiter_id);

-- ============================================
-- SEED DATA: Initial badges
-- ============================================

INSERT INTO badges (id, code, name_japanese, name_english, description, criteria, category) VALUES
(uuid_generate_v4(), 'STANDUP_MASTER', '朝会マスター', 'Standup Master', 'Can report progress fluently in 60 seconds', 'Complete 10+ Shadow Days with standup score > 80%', 'SHADOW_DAY'),
(uuid_generate_v4(), 'PR_WRITER', 'PRライター', 'PR Writer', 'Can document code in Japanese', 'Write 20+ PR descriptions evaluated at 85%+', 'CODE_JAPANESE'),
(uuid_generate_v4(), 'BUSINESS_EMAIL', 'ビジネスメール', 'Business Email', 'Can communicate professionally by email', 'Master 5 types of professional emails', 'CULTURAL'),
(uuid_generate_v4(), 'TECH_PRESENTER', '技術プレゼン', 'Tech Presenter', 'Can explain architecture in Japanese', 'Complete 3+ technical presentations in Japanese', 'CULTURAL'),
(uuid_generate_v4(), 'CULTURAL_NAVIGATOR', '文化ナビゲーター', 'Cultural Navigator', 'Navigates hierarchical interactions correctly', 'Cultural Intelligence Score > 85', 'CULTURAL'),
(uuid_generate_v4(), 'INTERVIEW_READY', '面接レディ', 'Interview Ready', 'Ready for a technical interview in Japanese', 'Complete 10+ simulations with score > 75%', 'INTERVIEW'),
(uuid_generate_v4(), 'QIITA_READER', 'Qiitaリーダー', 'Qiita Reader', 'Can consume Japanese technical documentation', 'Read 50+ technical articles with comprehension > 80%', 'REAL_CONTENT');
