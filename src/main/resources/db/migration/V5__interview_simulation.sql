-- Interview Questions Bank
CREATE TABLE interview_questions (
    id UUID PRIMARY KEY,
    interview_type VARCHAR(50) NOT NULL,
    difficulty VARCHAR(50) NOT NULL,
    phase VARCHAR(50) NOT NULL,
    content TEXT NOT NULL,
    content_japanese TEXT,
    model_answer TEXT,
    expected_keywords TEXT,
    scoring_criteria TEXT,
    time_limit_seconds INT DEFAULT 120,
    order_index INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT NOW()
);

CREATE INDEX idx_interview_questions_type_difficulty ON interview_questions(interview_type, difficulty);

-- Interview Sessions
CREATE TABLE interview_sessions (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    interview_type VARCHAR(50) NOT NULL,
    difficulty VARCHAR(50) NOT NULL,
    current_phase VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    current_question_index INT DEFAULT 0,
    total_questions INT DEFAULT 0,
    language_score DOUBLE PRECISION DEFAULT 0,
    technical_score DOUBLE PRECISION DEFAULT 0,
    communication_score DOUBLE PRECISION DEFAULT 0,
    cultural_score DOUBLE PRECISION DEFAULT 0,
    overall_score DOUBLE PRECISION DEFAULT 0,
    total_time_spent_seconds INT DEFAULT 0,
    passed BOOLEAN DEFAULT FALSE,
    question_ids TEXT,
    started_at TIMESTAMP DEFAULT NOW(),
    completed_at TIMESTAMP
);

CREATE INDEX idx_interview_sessions_user ON interview_sessions(user_id);
CREATE INDEX idx_interview_sessions_status ON interview_sessions(status);

-- Interview Answers
CREATE TABLE interview_answers (
    id UUID PRIMARY KEY,
    session_id UUID NOT NULL REFERENCES interview_sessions(id),
    question_id UUID NOT NULL REFERENCES interview_questions(id),
    answer_text TEXT NOT NULL,
    time_spent_seconds INT DEFAULT 0,
    language_score DOUBLE PRECISION DEFAULT 0,
    technical_score DOUBLE PRECISION DEFAULT 0,
    communication_score DOUBLE PRECISION DEFAULT 0,
    cultural_score DOUBLE PRECISION DEFAULT 0,
    overall_score DOUBLE PRECISION DEFAULT 0,
    submitted_at TIMESTAMP DEFAULT NOW()
);

CREATE INDEX idx_interview_answers_session ON interview_answers(session_id);

-- Seed initial interview questions
INSERT INTO interview_questions (id, interview_type, difficulty, phase, content, content_japanese, model_answer, expected_keywords, scoring_criteria, time_limit_seconds, order_index) VALUES
-- SELF_INTRODUCTION - BEGINNER
(gen_random_uuid(), 'SELF_INTRODUCTION', 'BEGINNER', 'INTRODUCTION', 'Please introduce yourself', '自己紹介をお願いします', '私は[名前]と申します。[大学/会社]で[専門]を勉強しています。趣味は[趣味]です。よろしくお願いします。', '名前||専門||趣味||よろしく', 'Clear name, background, and closing', 120, 1),
(gen_random_uuid(), 'SELF_INTRODUCTION', 'BEGINNER', 'MAIN_QUESTIONS', 'Why are you interested in Japan?', '日本に興味を持った理由は何ですか？', '日本の技術力と文化に興味があります。特にIT業界での日本の革新的な取り組みに魅力を感じています。', '日本||技術||文化||興味', 'Personal motivation clearly stated', 120, 2),
-- HR_JAPANESE - BEGINNER
(gen_random_uuid(), 'HR_JAPANESE', 'BEGINNER', 'INTRODUCTION', 'What is your motivation for applying?', '志望動機を教えてください', '御社の技術力と成長環境に魅力を感じています。特に[具体的な理由]に惹かれました。', '御社||技術||成長||魅力', 'Specific company reference and personal fit', 150, 1),
(gen_random_uuid(), 'HR_JAPANESE', 'BEGINNER', 'MAIN_QUESTIONS', 'What are your strengths?', 'あなたの長所は何ですか？', '私の長所は問題解決能力です。複雑な課題に対して論理的にアプローチし、チームと協力して解決することが得意です。', '長所||問題解決||チーム||論理的', 'Concrete example preferred', 120, 2),
(gen_random_uuid(), 'HR_JAPANESE', 'BEGINNER', 'MAIN_QUESTIONS', 'What are your weaknesses?', 'あなたの短所は何ですか？', '完璧主義なところがあり、時々細部にこだわりすぎてしまいます。しかし、優先順位を意識して改善しています。', '短所||改善||意識||努力', 'Show self-awareness and improvement effort', 120, 3),
-- TECH_JAVA - BEGINNER
(gen_random_uuid(), 'TECH_JAVA', 'BEGINNER', 'MAIN_QUESTIONS', 'Explain the difference between abstract class and interface in Java', 'Javaにおけるabstractクラスとinterfaceの違いを説明してください', 'Abstract classは部分的な実装を持てますが、interfaceはJava 8以降defaultメソッドを除いて実装を持ちません。クラスは一つのabstract classしか継承できませんが、複数のinterfaceを実装できます。', 'abstract||interface||継承||実装||multiple', 'Technical accuracy and clarity', 150, 1),
(gen_random_uuid(), 'TECH_JAVA', 'BEGINNER', 'MAIN_QUESTIONS', 'What is the difference between == and equals() in Java?', 'Javaの==とequals()の違いは何ですか？', '==は参照の比較で、同じオブジェクトを指しているか確認します。equals()は内容の比較で、オブジェクトの値が等しいか確認します。', '参照||内容||比較||オブジェクト||値', 'Reference vs value comparison', 120, 2),
-- TECH_SPRING - INTERMEDIATE
(gen_random_uuid(), 'TECH_SPRING', 'INTERMEDIATE', 'MAIN_QUESTIONS', 'Explain Dependency Injection in Spring', 'SpringのDI（依存性注入）について説明してください', 'DIはオブジェクト間の依存関係をSpringコンテナが管理するパターンです。コンストラクタインジェクション、セッターインジェクション、フィールドインジェクションの3種類があります。', 'DI||コンテナ||コンストラクタ||依存関係||IoC', 'Understanding of IoC principle', 150, 1),
(gen_random_uuid(), 'TECH_SPRING', 'INTERMEDIATE', 'MAIN_QUESTIONS', 'What is the difference between @Component, @Service, @Repository?', '@Component、@Service、@Repositoryの違いは？', '全てSpringのBean登録アノテーションですが、意味的に異なります。@Componentは汎用、@Serviceはビジネスロジック層、@Repositoryはデータアクセス層で例外変換も行います。', '@Component||@Service||@Repository||層||Bean', 'Semantic differences and layer separation', 120, 2),
-- BEHAVIORAL - INTERMEDIATE
(gen_random_uuid(), 'BEHAVIORAL', 'INTERMEDIATE', 'MAIN_QUESTIONS', 'Tell me about a time you solved a difficult problem', '困難な問題を解決した経験を教えてください', '以前のプロジェクトで[状況]がありました。[課題]に対して、[行動]を取り、結果として[成果]を達成しました。', '状況||課題||行動||結果||成果', 'STAR method structure', 180, 1),
(gen_random_uuid(), 'BEHAVIORAL', 'INTERMEDIATE', 'MAIN_QUESTIONS', 'How do you handle disagreements with team members?', 'チームメンバーとの意見の相違にどう対処しますか？', 'まず相手の意見をしっかり聞き、理解します。その上で、データや事実に基づいて建設的な議論を行います。', '聞く||理解||データ||建設的||議論', 'Communication and collaboration focus', 150, 2),
-- BUSINESS_JAPANESE - INTERMEDIATE
(gen_random_uuid(), 'BUSINESS_JAPANESE', 'INTERMEDIATE', 'MAIN_QUESTIONS', 'How would you write a business email requesting a meeting?', '会議を依頼するビジネスメールをどのように書きますか？', 'お忙しいところ恐れ入りますが、[件名]についてお打ち合わせのお時間をいただけますでしょうか。ご都合の良い日時をご教示いただければ幸いです。', 'お忙しい||恐れ入ります||いただけますでしょうか||ご都合||幸いです', 'Proper keigo and business format', 150, 1),
(gen_random_uuid(), 'BUSINESS_JAPANESE', 'INTERMEDIATE', 'MAIN_QUESTIONS', 'How do you report a problem to your manager?', '上司に問題を報告する際、どのように伝えますか？', 'お時間をいただきありがとうございます。[問題]について報告させていただきます。現状は[状況]で、[対策案]を検討しております。', '報告||させていただきます||現状||対策||検討', 'Problem + current status + proposed solution', 150, 2);
