-- V4: Quiz enhancements — adaptive difficulty, modes, attempts, results

-- Add columns to existing quizzes table
ALTER TABLE quizzes ADD COLUMN mode VARCHAR(50) DEFAULT 'CLASSIC';
ALTER TABLE quizzes ADD COLUMN description TEXT;
ALTER TABLE quizzes ADD COLUMN max_attempts INT DEFAULT 0;
ALTER TABLE quizzes ADD COLUMN passing_score INT DEFAULT 60;
ALTER TABLE quizzes ADD COLUMN is_published BOOLEAN DEFAULT TRUE;
ALTER TABLE quizzes ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

-- Add columns to existing questions table
ALTER TABLE questions ADD COLUMN points INT DEFAULT 1;
ALTER TABLE questions ADD COLUMN difficulty_level VARCHAR(50) DEFAULT 'MEDIUM';
ALTER TABLE questions ADD COLUMN time_limit_seconds INT;
ALTER TABLE questions ADD COLUMN created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

-- Quiz Attempts (user session)
CREATE TABLE quiz_attempts (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    quiz_id UUID NOT NULL REFERENCES quizzes(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    mode VARCHAR(50) NOT NULL DEFAULT 'CLASSIC',
    status VARCHAR(50) NOT NULL DEFAULT 'IN_PROGRESS',
    current_streak INT DEFAULT 0,
    max_streak INT DEFAULT 0,
    lives_remaining INT DEFAULT 3,
    started_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP,
    time_spent_seconds INT DEFAULT 0
);

-- Individual answer submissions
CREATE TABLE quiz_answer_submissions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    attempt_id UUID NOT NULL REFERENCES quiz_attempts(id) ON DELETE CASCADE,
    question_id UUID NOT NULL REFERENCES questions(id) ON DELETE CASCADE,
    user_answer TEXT,
    is_correct BOOLEAN NOT NULL DEFAULT FALSE,
    points_earned DOUBLE PRECISION DEFAULT 0,
    time_spent_seconds INT DEFAULT 0,
    streak_at_time INT DEFAULT 0,
    difficulty_at_time VARCHAR(50),
    submitted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Quiz Results (final score + analytics)
CREATE TABLE quiz_results (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    attempt_id UUID UNIQUE NOT NULL REFERENCES quiz_attempts(id) ON DELETE CASCADE,
    quiz_id UUID NOT NULL REFERENCES quizzes(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    total_questions INT NOT NULL,
    correct_answers INT NOT NULL DEFAULT 0,
    total_score DOUBLE PRECISION NOT NULL DEFAULT 0,
    max_possible_score DOUBLE PRECISION NOT NULL DEFAULT 0,
    percentage DOUBLE PRECISION NOT NULL DEFAULT 0,
    passed BOOLEAN NOT NULL DEFAULT FALSE,
    max_streak INT DEFAULT 0,
    average_time_per_question DOUBLE PRECISION DEFAULT 0,
    difficulty_reached VARCHAR(50) DEFAULT 'MEDIUM',
    completed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_quiz_attempts_user ON quiz_attempts(user_id);
CREATE INDEX idx_quiz_attempts_quiz ON quiz_attempts(quiz_id);
CREATE INDEX idx_quiz_attempts_status ON quiz_attempts(status);
CREATE INDEX idx_quiz_answer_submissions_attempt ON quiz_answer_submissions(attempt_id);
CREATE INDEX idx_quiz_results_user ON quiz_results(user_id);
CREATE INDEX idx_quiz_results_quiz ON quiz_results(quiz_id);
