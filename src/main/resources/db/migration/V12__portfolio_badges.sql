-- Portfolio & Recruiter: Badges, UserBadges, PublicProfiles

CREATE TABLE badges (
    id UUID PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    name_jp VARCHAR(200) NOT NULL,
    name_en VARCHAR(200) NOT NULL,
    description_jp TEXT,
    description_en TEXT,
    icon_url VARCHAR(500),
    category VARCHAR(30) NOT NULL,
    rarity VARCHAR(20) NOT NULL,
    related_module VARCHAR(30),
    required_score INT DEFAULT 0,
    required_count INT DEFAULT 0,
    xp_reward INT DEFAULT 0
);

CREATE TABLE user_badges (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    badge_id UUID NOT NULL REFERENCES badges(id),
    earned_at TIMESTAMP NOT NULL DEFAULT NOW(),
    showcased BOOLEAN NOT NULL DEFAULT FALSE,
    UNIQUE(user_id, badge_id)
);

CREATE INDEX idx_user_badges_user_id ON user_badges(user_id);
CREATE INDEX idx_user_badges_badge_id ON user_badges(badge_id);

CREATE TABLE public_profiles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL UNIQUE,
    display_name VARCHAR(100) NOT NULL,
    slug VARCHAR(100) NOT NULL UNIQUE,
    bio TEXT,
    avatar_url VARCHAR(500),
    current_level VARCHAR(20),
    visibility VARCHAR(20) NOT NULL DEFAULT 'PUBLIC',
    open_to_work BOOLEAN NOT NULL DEFAULT FALSE,
    preferred_role VARCHAR(100),
    location VARCHAR(100),
    total_xp INT DEFAULT 0,
    total_badges INT DEFAULT 0,
    lessons_completed INT DEFAULT 0,
    reading_sessions_completed INT DEFAULT 0,
    average_score DOUBLE PRECISION DEFAULT 0,
    highlighted_skills JSONB DEFAULT '[]',
    showcased_badge_ids JSONB DEFAULT '[]',
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_public_profiles_user_id ON public_profiles(user_id);
CREATE INDEX idx_public_profiles_slug ON public_profiles(slug);
CREATE INDEX idx_public_profiles_visibility ON public_profiles(visibility);
CREATE INDEX idx_public_profiles_open_to_work ON public_profiles(open_to_work);
CREATE INDEX idx_public_profiles_current_level ON public_profiles(current_level);

-- Seed badge catalog
INSERT INTO badges (id, code, name_jp, name_en, description_jp, description_en, category, rarity, related_module, required_score, required_count, xp_reward) VALUES
-- Milestone badges
(gen_random_uuid(), 'FIRST_LESSON', '初めの一歩', 'First Step', '最初のレッスンを完了した', 'Completed your first lesson', 'MILESTONE', 'COMMON', 'LESSON', 0, 1, 50),
(gen_random_uuid(), 'LESSON_10', '学習者', 'Learner', '10個のレッスンを完了した', 'Completed 10 lessons', 'MILESTONE', 'UNCOMMON', 'LESSON', 0, 10, 100),
(gen_random_uuid(), 'LESSON_50', '熟練者', 'Veteran', '50個のレッスンを完了した', 'Completed 50 lessons', 'MILESTONE', 'RARE', 'LESSON', 0, 50, 300),
(gen_random_uuid(), 'LESSON_100', '達人', 'Master', '100個のレッスンを完了した', 'Completed 100 lessons', 'MILESTONE', 'EPIC', 'LESSON', 0, 100, 500),

-- Streak badges
(gen_random_uuid(), 'STREAK_7', '一週間連続', 'Week Warrior', '7日間連続で学習した', 'Studied 7 days in a row', 'STREAK', 'UNCOMMON', NULL, 0, 7, 150),
(gen_random_uuid(), 'STREAK_30', '月間チャンピオン', 'Monthly Champion', '30日間連続で学習した', 'Studied 30 days in a row', 'STREAK', 'RARE', NULL, 0, 30, 500),
(gen_random_uuid(), 'STREAK_100', '鉄の意志', 'Iron Will', '100日間連続で学習した', 'Studied 100 days in a row', 'STREAK', 'LEGENDARY', NULL, 0, 100, 1000),

-- Mastery badges
(gen_random_uuid(), 'QUIZ_PERFECT', '完璧主義者', 'Perfectionist', 'クイズで満点を取った', 'Got a perfect score on a quiz', 'MASTERY', 'UNCOMMON', 'QUIZ', 100, 1, 200),
(gen_random_uuid(), 'INTERVIEW_ACE', '面接の達人', 'Interview Ace', '模擬面接で高得点を獲得した', 'Scored high on a mock interview', 'MASTERY', 'RARE', 'INTERVIEW', 90, 1, 300),
(gen_random_uuid(), 'N2_ACHIEVED', 'N2合格', 'N2 Achieved', 'N2レベルに到達した', 'Reached N2 level proficiency', 'MASTERY', 'EPIC', NULL, 0, 0, 750),
(gen_random_uuid(), 'N1_ACHIEVED', 'N1合格', 'N1 Achieved', 'N1レベルに到達した', 'Reached N1 level proficiency', 'MASTERY', 'LEGENDARY', NULL, 0, 0, 1500),

-- Specialization badges
(gen_random_uuid(), 'CODE_REVIEWER', 'コードレビュアー', 'Code Reviewer', 'コードレビュー演習を10個完了した', 'Completed 10 code review exercises', 'SPECIALIZATION', 'RARE', 'CODE_REVIEW', 0, 10, 400),
(gen_random_uuid(), 'CONTENT_READER', '読書家', 'Bookworm', 'リアルコンテンツを20記事読了した', 'Read 20 real content articles', 'SPECIALIZATION', 'RARE', 'REAL_CONTENT', 0, 20, 400),
(gen_random_uuid(), 'KEIGO_MASTER', '敬語マスター', 'Keigo Master', '敬語関連の演習を全て完了した', 'Completed all keigo exercises', 'SPECIALIZATION', 'EPIC', 'INTERVIEW', 0, 15, 600),

-- Community badges
(gen_random_uuid(), 'EARLY_ADOPTER', 'アーリーアダプター', 'Early Adopter', 'プラットフォーム初期メンバー', 'Early platform member', 'COMMUNITY', 'RARE', NULL, 0, 0, 200),
(gen_random_uuid(), 'TOP_CONTRIBUTOR', 'トップ貢献者', 'Top Contributor', 'コミュニティへの貢献が認められた', 'Recognized for community contributions', 'COMMUNITY', 'LEGENDARY', NULL, 0, 0, 1000);
