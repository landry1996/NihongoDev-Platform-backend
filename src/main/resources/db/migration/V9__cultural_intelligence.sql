-- V9: Cultural Intelligence Module (BLOC 11)
-- Tables: cultural_scenarios, scenario_attempts, cultural_progress

-- ============================================================
-- Table 1: cultural_scenarios
-- ============================================================
CREATE TABLE cultural_scenarios (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title VARCHAR(300) NOT NULL,
    title_jp VARCHAR(300) NOT NULL,
    situation TEXT NOT NULL,
    situation_jp TEXT NOT NULL,
    context VARCHAR(50) NOT NULL,
    relationship VARCHAR(50) NOT NULL,
    mode VARCHAR(50) NOT NULL,
    category VARCHAR(50) NOT NULL,
    expected_keigo_level VARCHAR(50) NOT NULL,
    difficulty VARCHAR(50) NOT NULL,
    choices JSONB,
    model_answer TEXT,
    model_answer_explanation TEXT,
    key_phrases JSONB,
    avoid_phrases JSONB,
    cultural_note TEXT,
    xp_reward INTEGER NOT NULL DEFAULT 50,
    is_published BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_cultural_scenarios_context ON cultural_scenarios(context);
CREATE INDEX idx_cultural_scenarios_relationship ON cultural_scenarios(relationship);
CREATE INDEX idx_cultural_scenarios_category ON cultural_scenarios(category);
CREATE INDEX idx_cultural_scenarios_difficulty ON cultural_scenarios(difficulty);
CREATE INDEX idx_cultural_scenarios_published ON cultural_scenarios(is_published);

-- ============================================================
-- Table 2: scenario_attempts
-- ============================================================
CREATE TABLE scenario_attempts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id),
    scenario_id UUID NOT NULL REFERENCES cultural_scenarios(id),
    user_response TEXT,
    selected_choice_id UUID,
    keigo_score INTEGER NOT NULL DEFAULT 0,
    appropriateness_score INTEGER NOT NULL DEFAULT 0,
    uchi_soto_score INTEGER NOT NULL DEFAULT 0,
    indirectness_score INTEGER NOT NULL DEFAULT 0,
    professional_tone_score INTEGER NOT NULL DEFAULT 0,
    overall_score INTEGER NOT NULL DEFAULT 0,
    violations JSONB,
    feedback TEXT,
    time_spent_seconds INTEGER NOT NULL DEFAULT 0,
    attempted_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_scenario_attempts_user ON scenario_attempts(user_id);
CREATE INDEX idx_scenario_attempts_scenario ON scenario_attempts(scenario_id);
CREATE INDEX idx_scenario_attempts_user_scenario ON scenario_attempts(user_id, scenario_id);
CREATE INDEX idx_scenario_attempts_attempted_at ON scenario_attempts(attempted_at);

-- ============================================================
-- Table 3: cultural_progress
-- ============================================================
CREATE TABLE cultural_progress (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id),
    category VARCHAR(50) NOT NULL,
    scenarios_completed INTEGER NOT NULL DEFAULT 0,
    total_score INTEGER NOT NULL DEFAULT 0,
    average_score INTEGER NOT NULL DEFAULT 0,
    best_score INTEGER NOT NULL DEFAULT 0,
    current_streak INTEGER NOT NULL DEFAULT 0,
    unlocked_level VARCHAR(50) NOT NULL DEFAULT 'TEINEIGO',
    last_activity_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_cultural_progress_user_category UNIQUE (user_id, category)
);

CREATE INDEX idx_cultural_progress_user ON cultural_progress(user_id);

-- ============================================================
-- Seed data: 12 cultural scenarios
-- ============================================================
INSERT INTO cultural_scenarios (id, title, title_jp, situation, situation_jp, context, relationship, mode, category, expected_keigo_level, difficulty, choices, model_answer, model_answer_explanation, key_phrases, avoid_phrases, cultural_note, xp_reward, is_published) VALUES

-- 1. MEETING / COMMUNICATION / MULTIPLE_CHOICE
(gen_random_uuid(),
 'Disagreeing with a Senior Engineer in a Meeting',
 '会議で先輩エンジニアに反対意見を述べる',
 'During a technical design review meeting, your senior engineer proposes using a monolithic architecture. You believe a microservices approach would be better for the project. How do you express your disagreement?',
 '技術設計レビュー会議で、先輩エンジニアがモノリシックアーキテクチャを提案しています。あなたはマイクロサービスの方がプロジェクトに適していると考えています。どのように反対意見を述べますか？',
 'MEETING', 'TO_SUPERIOR', 'MULTIPLE_CHOICE', 'COMMUNICATION', 'SONKEIGO', 'N3',
 '[{"text":"I think you are wrong. Microservices is clearly better.","textJp":"間違っていると思います。マイクロサービスの方が明らかに良いです。","isOptimal":false,"culturalScore":10,"feedbackIfChosen":"Too direct and confrontational. In Japanese workplace culture, directly saying someone is wrong (especially a superior) damages the relationship and causes loss of face."},{"text":"Thank you for your proposal. I have been studying this topic and would like to humbly share one perspective — would a microservices approach perhaps also be worth considering?","textJp":"ご提案ありがとうございます。私もこのテーマについて勉強させていただいておりまして、一つの視点を共有させていただければと思うのですが、マイクロサービスのアプローチも検討する価値があるのではないかと考えております。","isOptimal":true,"culturalScore":95,"feedbackIfChosen":"Excellent! You acknowledged the senior''s proposal with gratitude, positioned yourself humbly, used indirect language (ではないかと), and framed your suggestion as an addition rather than a replacement."},{"text":"That might work, but have you considered microservices?","textJp":"それもいいかもしれませんが、マイクロサービスは考えましたか？","isOptimal":false,"culturalScore":40,"feedbackIfChosen":"While not openly rude, this is still too direct for speaking to a superior. The question form implies they haven''t done their research. Lacks proper keigo and humility markers."},{"text":"I completely agree with your approach. (Say nothing about your real opinion)","textJp":"おっしゃる通りだと思います。（自分の本当の意見は言わない）","isOptimal":false,"culturalScore":50,"feedbackIfChosen":"While showing deference, completely suppressing your professional opinion is not ideal either. Japanese business culture values harmony but also expects constructive contributions through indirect communication."}]',
 NULL, NULL,
 '["ご提案ありがとうございます", "勉強させていただいて", "共有させていただければ", "ではないかと考えております", "検討する価値がある"]',
 '["間違っている", "それは違う", "考えましたか", "でも", "しかし（文頭で）"]',
 'In Japanese meetings, disagreeing with superiors requires layered indirectness: first acknowledge their point, then position your counter-opinion as a humble addition or question. The pattern is: gratitude → humble framing → indirect suggestion (〜ではないでしょうか).',
 60, true),

-- 2. EMAIL / REPORTING / MULTIPLE_CHOICE
(gen_random_uuid(),
 'Reporting a Delayed Deadline to Your Manager',
 '上司に納期遅延を報告するメール',
 'Your feature development is running 3 days behind schedule due to unexpected technical issues. You need to inform your manager (部長) via email. How do you write this email?',
 '技術的な予期しない問題により、機能開発が予定より3日遅れています。部長にメールで報告する必要があります。どのようにメールを書きますか？',
 'EMAIL', 'TO_SUPERIOR', 'MULTIPLE_CHOICE', 'REPORTING', 'KENJOUGO', 'N2',
 '[{"text":"Subject: Delay notification. The feature will be 3 days late because of technical problems. Sorry.","textJp":"件名：遅延のお知らせ。技術的な問題で機能が3日遅れます。すみません。","isOptimal":false,"culturalScore":20,"feedbackIfChosen":"Far too casual for reporting to a 部長. Missing proper email structure, keigo, responsibility acknowledgment, and recovery plan. The apology is too informal."},{"text":"Subject: [Report] Regarding the schedule for Feature X development. Body: Thank you for your continued guidance. I sincerely apologize for the inconvenience, but I must report a matter regarding the delivery schedule. Due to [specific issue], a 3-day delay is anticipated. I take full responsibility for insufficient planning. I have prepared the following recovery plan: [plan]. I will do my utmost to minimize the impact.","textJp":"件名：【ご報告】機能X開発のスケジュールについて。本文：いつもご指導いただきありがとうございます。大変恐れ入りますが、納期に関するご報告がございます。[具体的問題]により、3日間の遅延が見込まれる状況でございます。見通しの甘さを深くお詫び申し上げます。つきましては、以下のリカバリープランをご提案させていただきます：[計画]。影響を最小限に抑えるよう全力を尽くしてまいります。","isOptimal":true,"culturalScore":95,"feedbackIfChosen":"Perfect structure for bad-news reporting in Japanese business: proper subject prefix [ご報告], gratitude opening, apologetic framing before the news, taking personal responsibility, and presenting a concrete recovery plan. Shows 報連相 (horenso) mastery."},{"text":"Subject: Schedule update. Hi Manager, just a heads up that we are running a bit behind. Should be about 3 days. Let me know if you have questions.","textJp":"件名：スケジュール更新。部長、少し遅れていることをお知らせします。3日くらいです。質問があれば教えてください。","isOptimal":false,"culturalScore":15,"feedbackIfChosen":"Extremely inappropriate register for communicating with a 部長 about a delay. Casual tone, no keigo, no apology, no recovery plan, and ''let me know if you have questions'' reverses the power dynamic inappropriately."}]',
 NULL, NULL,
 '["ご報告がございます", "大変恐れ入りますが", "お詫び申し上げます", "リカバリープランをご提案させていただきます", "全力を尽くしてまいります"]',
 '["遅れます（plain form）", "すみません（casual）", "ちょっと", "〜くらい", "教えてください"]',
 '報連相 (Ho-Ren-So: Report, Contact, Consult) is fundamental in Japanese companies. When reporting bad news, always: (1) apologize first, (2) explain concisely, (3) take responsibility, (4) propose a solution. Never leave the superior without a path forward.',
 70, true),

-- 3. NOMIKAI / SOCIAL / MULTIPLE_CHOICE
(gen_random_uuid(),
 'First Nomikai with the Team',
 'チームとの初めての飲み会',
 'You have been invited to your first nomikai (drinking party) with your new team. Your manager and several senior engineers are present. The first round of drinks has arrived. What is the appropriate behavior?',
 '新しいチームとの初めての飲み会に誘われました。上司や先輩エンジニアが複数参加しています。最初の飲み物が届きました。適切な振る舞いは何ですか？',
 'NOMIKAI', 'GROUP_MIXED', 'MULTIPLE_CHOICE', 'SOCIAL', 'TEINEIGO', 'N4',
 '[{"text":"Wait for the most senior person to give the opening toast (kanpai) before drinking. Pour drinks for seniors first, holding the bottle with both hands. Keep your glass lower than theirs when clinking.","textJp":"最も上位の方が乾杯の音頭を取るまで待つ。先輩方にまず両手でお酒を注ぐ。グラスを合わせる際は、相手より低い位置で合わせる。","isOptimal":true,"culturalScore":95,"feedbackIfChosen":"Perfect! You demonstrated understanding of nomikai etiquette: waiting for kanpai, serving others first (especially seniors), the two-hand pour, and lowering your glass as a sign of respect."},{"text":"Start drinking immediately since the drinks are getting warm. You paid for your share, so you can enjoy it your way.","textJp":"飲み物がぬるくなるので、すぐに飲み始める。自分の分は自分で払っているのだから、自分のペースで楽しむ。","isOptimal":false,"culturalScore":5,"feedbackIfChosen":"This completely ignores Japanese group drinking customs. Starting before kanpai is considered very rude. The individualistic mindset clashes with the collectivist nature of nomikai."},{"text":"Wait for the toast, then focus on your own drink and food for the evening. You are there to eat and relax.","textJp":"乾杯を待ってから、一晩中自分の飲み物と食べ物に集中する。食べてリラックスするために来たのだから。","isOptimal":false,"culturalScore":35,"feedbackIfChosen":"Waiting for the toast is correct, but nomikai is primarily a social bonding event, not just eating and drinking. You should actively pour drinks for others, make conversation, and show attentiveness to the group."}]',
 NULL, NULL,
 '["乾杯を待つ", "お注ぎしましょうか", "両手で注ぐ", "グラスを低くする", "お疲れ様です"]',
 '["先に飲む", "自分で注ぐ（上司の前で）", "一人で食べる"]',
 'Nomikai is not just about drinking — it is about building 信頼関係 (trust relationships). Pouring for others (お酌) shows awareness and care. The hierarchical seating (上座/下座) and serving order reinforce team bonds. New members should be especially attentive to pouring for seniors.',
 50, true),

-- 4. STANDUP / COMMUNICATION / FREE_TEXT
(gen_random_uuid(),
 'Reporting a Blocker in Daily Standup',
 '朝会でブロッカーを報告する',
 'During the daily standup, you need to report that you are blocked because a senior colleague has not reviewed your pull request for 2 days. You need to mention this without sounding like you are blaming them.',
 '朝会で、先輩が2日間プルリクエストをレビューしていないためブロックされていることを報告する必要があります。相手を責めるように聞こえないように伝える必要があります。',
 'STANDUP', 'GROUP_MIXED', 'FREE_TEXT', 'COMMUNICATION', 'TEINEIGO', 'N3',
 NULL,
 '昨日の作業はPR#123の対応をしておりました。現在、レビュー待ちの状況でして、お忙しいところ恐縮ですが、もしお時間がありましたらご確認いただけると大変助かります。本日はその間に別のタスクを進める予定です。',
 'This model answer avoids naming the person directly, uses passive/situational framing (レビュー待ちの状況), acknowledges their busyness (お忙しいところ), and frames the request as helpful rather than demanding. It also shows initiative by mentioning alternative work.',
 '["レビュー待ちの状況", "お忙しいところ恐縮ですが", "ご確認いただけると助かります", "その間に別のタスクを進める"]',
 '["まだレビューされていません", "○○さんが遅い", "待たされています", "困っています"]',
 'In Japanese standup culture, never directly name someone as the cause of a delay. Use passive/situational language (状況でして) and frame requests as favors (いただけると助かります). Always show that you are being proactive with alternative work.',
 55, true),

-- 5. CLIENT_VISIT / CEREMONY / MULTIPLE_CHOICE
(gen_random_uuid(),
 'Receiving Business Cards from a Client',
 'クライアントから名刺を受け取る',
 'You are meeting a client (取引先) for the first time at your office. They are a 部長 (department manager) from a partner company. They extend their business card to you. What is the correct protocol?',
 '初めてオフィスでクライアント（取引先）と会います。相手はパートナー企業の部長です。名刺を差し出されました。正しいプロトコルは何ですか？',
 'CLIENT_VISIT', 'TO_CLIENT', 'MULTIPLE_CHOICE', 'CEREMONY', 'SONKEIGO', 'N4',
 '[{"text":"Receive the card with both hands, bow slightly, read it carefully and say their name and title aloud, then place it respectfully on the table in front of you for the duration of the meeting. Never write on it.","textJp":"両手で名刺を受け取り、軽くお辞儀をし、名前と肩書きを声に出して確認し、会議中はテーブルの上に丁寧に置く。絶対に書き込まない。","isOptimal":true,"culturalScore":95,"feedbackIfChosen":"Excellent! You showed proper meishi koukan etiquette: both hands, bow, reading carefully (shows respect), verbal acknowledgment, and proper placement. The business card represents the person — treat it with the same respect."},{"text":"Take the card with one hand, glance at it, and put it in your pocket so you do not lose it.","textJp":"片手で名刺を受け取り、ちらっと見て、なくさないようにポケットに入れる。","isOptimal":false,"culturalScore":10,"feedbackIfChosen":"Multiple serious violations: one-handed reception is disrespectful, glancing suggests disinterest, and pocketing a card immediately (especially in a back pocket) is extremely rude — it symbolically means you are sitting on the person."},{"text":"Receive with both hands and bow, then immediately offer your card in exchange while still holding theirs.","textJp":"両手で受け取りお辞儀をし、相手の名刺を持ったまますぐに自分の名刺を渡す。","isOptimal":false,"culturalScore":50,"feedbackIfChosen":"The reception is correct, but you should not simultaneously hold their card while presenting yours. The proper sequence is: receive theirs fully, acknowledge it, then present yours. As the visitor is higher status (client + 部長), receive first."}]',
 NULL, NULL,
 '["両手で受け取る", "頂戴いたします", "○○部長でいらっしゃいますね", "テーブルに丁寧に置く"]',
 '["片手で取る", "ポケットに入れる", "折る", "書き込む", "名刺の上に物を置く"]',
 '名刺交換 (meishi koukan) is a ritual, not a mere information exchange. The card IS the person. Key rules: both hands always, bow, read thoroughly, never write on/fold/cover it. Place on table during meeting aligned to seating. Store in a proper card holder afterward.',
 50, true),

-- 6. ONE_ON_ONE / CONFLICT / FREE_TEXT
(gen_random_uuid(),
 'Addressing Overwork in a 1-on-1 with Your Manager',
 '上司との1on1で過重労働について話す',
 'You have been working overtime every day for 3 weeks and it is affecting your health. In your weekly 1-on-1 with your direct manager (課長), you want to request a more balanced workload without appearing weak or uncommitted.',
 '3週間毎日残業しており、健康に影響が出ています。直属の課長との週次1on1で、弱く見えたりコミットメントが低く見えたりせずに、バランスの取れた仕事量を要望したいです。',
 'ONE_ON_ONE', 'TO_SUPERIOR', 'FREE_TEXT', 'CONFLICT', 'TEINEIGO', 'N2',
 NULL,
 'いつもご指導いただきありがとうございます。ご相談させていただきたいことがあるのですが、最近の業務量について率直にお話しさせていただければと思います。チームに貢献したい気持ちは変わらないのですが、ここ3週間ほど残業が続いており、パフォーマンスの質を維持するために業務の優先順位について相談させていただけないでしょうか。例えば、タスクAとBの期限を調整いただくか、一部を分担させていただくことは可能でしょうか。',
 'This answer maintains commitment framing (貢献したい), uses consultation language (ご相談), focuses on quality/performance rather than personal complaint, and offers concrete solutions. It avoids the taboo of appearing to refuse work while still clearly communicating the issue.',
 '["ご相談させていただきたい", "率直にお話しさせていただければ", "パフォーマンスの質を維持する", "優先順位について相談", "可能でしょうか"]',
 '["残業したくない", "無理です", "多すぎます", "できません", "疲れました"]',
 'In Japanese work culture, directly refusing overtime or complaining about workload can be seen as lacking 根性 (perseverance). Frame the discussion around quality and team contribution, use 相談 (consultation) framing, and always propose solutions rather than just stating problems.',
 75, true),

-- 7. CODE_REVIEW / COMMUNICATION / FREE_TEXT
(gen_random_uuid(),
 'Giving Code Review Feedback to a Senior Developer',
 '先輩開発者にコードレビューのフィードバックをする',
 'You found a potential security vulnerability (SQL injection) in a pull request submitted by a senior developer (先輩). You need to comment on their PR requesting changes. How do you phrase your review comment?',
 '先輩が提出したプルリクエストに潜在的なセキュリティ脆弱性（SQLインジェクション）を見つけました。PRにコメントして修正を依頼する必要があります。どのようにレビューコメントを書きますか？',
 'CODE_REVIEW', 'TO_SUPERIOR', 'FREE_TEXT', 'COMMUNICATION', 'TEINEIGO', 'N3',
 NULL,
 'レビューさせていただきました。一点確認させていただきたいのですが、こちらの箇所でパラメータを直接クエリに組み込む形になっているようですが、PreparedStatementを使用する形に変更した方が安全性が高まるのではないかと思いました。もし私の認識が間違っていたら申し訳ございません。ご検討いただけますと幸いです。',
 'This model phrasing: (1) frames as a question/confirmation rather than a correction, (2) uses hedging language (ようですが, ではないかと), (3) adds self-deprecation (認識が間違っていたら), and (4) closes with a polite request. It points out the issue clearly while preserving face.',
 '["確認させていただきたい", "ではないかと思いました", "認識が間違っていたら申し訳ございません", "ご検討いただけますと幸いです"]',
 '["これはバグです", "修正してください", "間違っています", "SQLインジェクションがあります（直接的に）"]',
 'When reviewing senior developers'' code in Japan, frame issues as questions or observations, not directives. Use ではないか (wondering if perhaps...) and add self-deprecation. Even for critical bugs, the delivery must preserve 面子 (face). The indirectness does not diminish the technical content.',
 65, true),

-- 8. PHONE_CALL / COMMUNICATION / ROLE_PLAY
(gen_random_uuid(),
 'Answering a Client Phone Call for Your Absent Manager',
 '不在の上司宛のクライアントからの電話に対応する',
 'The phone rings at your desk. It is a call from a client (Mr. Tanaka from ABC Corp) asking for your manager (佐藤部長) who is currently in a meeting. You need to handle this call professionally.',
 '電話が鳴ります。ABC社の田中様からで、現在会議中の佐藤部長宛の電話です。プロフェッショナルに対応する必要があります。',
 'PHONE_CALL', 'TO_CLIENT', 'ROLE_PLAY', 'COMMUNICATION', 'KENJOUGO', 'N2',
 NULL,
 'お電話ありがとうございます。○○会社の△△でございます。田中様、いつもお世話になっております。申し訳ございません、佐藤はただいま会議に出ておりまして、席を外しております。会議は○時頃終了予定でございますが、終わり次第、佐藤から折り返しお電話させていただくよう申し伝えます。念のため、お電話番号を確認させていただいてもよろしいでしょうか。',
 'Key elements: (1) Company greeting with your name, (2) acknowledge the relationship (お世話になっております), (3) use uchi-soto correctly — remove honorifics from your manager''s name when speaking externally (佐藤, not 佐藤部長), (4) give a timeframe, (5) offer callback, (6) confirm contact info.',
 '["お電話ありがとうございます", "お世話になっております", "席を外しております", "折り返しお電話させていただく", "申し伝えます"]',
 '["佐藤部長は（外部に敬称使用）", "わかりません", "いません", "後でかけてください"]',
 'Critical uchi-soto rule: when speaking to external parties, NEVER use honorific titles for your own company members. Your 佐藤部長 becomes simply 佐藤 to outsiders. This is one of the most common mistakes foreigners make and is immediately noticed by Japanese clients.',
 70, true),

-- 9. ONBOARDING / SOCIAL / ROLE_PLAY
(gen_random_uuid(),
 'Self-Introduction on Your First Day',
 '入社初日の自己紹介',
 'It is your first day at a Japanese tech company. You are asked to give a brief self-introduction (自己紹介) to the entire engineering department (about 30 people including managers and directors). You are a foreign engineer.',
 '日本のテック企業での入社初日です。エンジニア部門全体（部長やディレクターを含む約30人）に簡単な自己紹介をするよう求められました。あなたは外国人エンジニアです。',
 'ONBOARDING', 'GROUP_MIXED', 'ROLE_PLAY', 'SOCIAL', 'TEINEIGO', 'N4',
 NULL,
 '皆様、初めまして。本日より入社いたしました○○と申します。○○出身で、前職では○○社にてバックエンド開発を○年間担当しておりました。日本語はまだまだ勉強中ですが、一日でも早く戦力になれるよう精一杯頑張りますので、ご指導ご鞭撻のほど、よろしくお願いいたします。',
 'Structure: greeting → name → origin → brief background → show humility about language → express eagerness to contribute → closing formula. The closing phrase ご指導ご鞭撻 is essential in onboarding introductions — it explicitly asks for guidance and shows you respect the team hierarchy.',
 '["初めまして", "入社いたしました", "○○と申します", "まだまだ勉強中", "精一杯頑張ります", "ご指導ご鞭撻のほど"]',
 '["俺は", "僕は（formal setting）", "日本語上手です", "すぐにできます", "簡単です"]',
 'First-day introductions set the tone for all future relationships. Key cultural points: show humility (まだまだ), express dedication (精一杯), request guidance (ご指導ご鞭撻). Avoid boasting about skills — let your work speak later. Keep it under 1 minute.',
 45, true),

-- 10. CONFLICT / NEGOTIATION / FREE_TEXT
(gen_random_uuid(),
 'Declining a Request from Another Team Without Damaging Relations',
 '他チームからの依頼を関係を損なわずに断る',
 'A peer from another team asks you to take on an additional task that is clearly outside your team''s scope and you are already at capacity. Your manager has told you to focus on current priorities. You need to decline without creating inter-team friction.',
 '他チームの同僚から、明らかにチームの範囲外かつすでにキャパシティいっぱいの追加タスクを依頼されました。上司からは現在の優先事項に集中するよう言われています。チーム間の摩擦を生まずに断る必要があります。',
 'CONFLICT', 'TO_PEER', 'FREE_TEXT', 'NEGOTIATION', 'TEINEIGO', 'N3',
 NULL,
 'ご依頼いただきありがとうございます。大変申し訳ないのですが、現在チームとして優先度の高い案件を複数抱えておりまして、すぐにお力添えするのが難しい状況です。上司にも確認してみますが、もし今期中は対応が難しい場合、○○チームの方が専門領域としてお力になれるかもしれません。何かほかにできることがあれば、お気軽にお声がけください。',
 'This response: (1) thanks for the trust, (2) explains the situation without blaming, (3) mentions checking with manager (shared responsibility), (4) offers an alternative resource, (5) leaves the door open for future collaboration. It never uses a flat "no" while being clearly a decline.',
 '["ご依頼ありがとうございます", "難しい状況です", "上司にも確認してみます", "お力になれるかもしれません", "お気軽にお声がけください"]',
 '["できません", "無理です", "それはうちの仕事じゃない", "忙しいので", "断ります"]',
 'Japanese workplace culture avoids direct refusals (断る). Instead, use 難しい (difficult) or ちょっと... (trailing off). Always: thank → explain situation → offer alternative → maintain relationship. The phrase 検討させてください can also buy time without committing.',
 60, true),

-- 11. MEETING / REPORTING / ROLE_PLAY
(gen_random_uuid(),
 'Presenting Bad Sprint Results to Stakeholders',
 'スプリントの悪い結果をステークホルダーに報告する',
 'You are the scrum master presenting sprint review results to stakeholders including a 本部長 (division head). The team completed only 60% of committed stories due to unexpected technical debt. You need to present this honestly while maintaining confidence in the team.',
 'スクラムマスターとして、本部長を含むステークホルダーにスプリントレビューの結果を発表します。予期しない技術的負債により、コミットしたストーリーの60%しか完了できませんでした。チームへの信頼を維持しながら正直に報告する必要があります。',
 'MEETING', 'TO_SUPERIOR', 'ROLE_PLAY', 'REPORTING', 'SONKEIGO', 'N2',
 NULL,
 'ご報告させていただきます。今スプリントの実績でございますが、計画した10ストーリーのうち6ストーリーを完了いたしました。未完了分につきましては、想定以上の技術的負債が発覚したことが主要因でございます。チームとして見積もりの精度に課題があったと認識しており、次スプリントでは以下の改善策を実施いたします。第一に、負債解消のための専用ストーリーを設定します。第二に、見積もり時にバッファを設ける方針といたします。今後このような事態を繰り返さないよう、チーム一丸となって取り組んでまいります。',
 'Structure for bad-news presentations: state facts → acknowledge cause → take team responsibility → present concrete countermeasures → express future commitment. Never blame individuals. The phrase チーム一丸となって shows collective responsibility.',
 '["ご報告させていただきます", "課題があったと認識しており", "改善策を実施いたします", "チーム一丸となって", "取り組んでまいります"]',
 '["言い訳になりますが", "○○さんのせいで", "仕方がない", "できませんでした（without countermeasure）"]',
 'In Japanese corporate reporting, bad results require: honest acknowledgment + 原因分析 (root cause) + 対策 (countermeasures) + 再発防止 (prevention commitment). Never present problems without solutions. Taking collective responsibility (we, not they) shows leadership maturity.',
 80, true),

-- 12. EMAIL / NEGOTIATION / MULTIPLE_CHOICE
(gen_random_uuid(),
 'Requesting a Deadline Extension from a Client',
 'クライアントに納期延長を依頼する',
 'Your team needs 5 additional business days to deliver a project milestone to an important client. You must write an email requesting this extension. The client is a 取締役 (board member) of the partner company.',
 'チームがプロジェクトのマイルストーンを重要なクライアントに納品するために、5営業日の追加が必要です。延長を依頼するメールを書く必要があります。クライアントはパートナー企業の取締役です。',
 'EMAIL', 'TO_CLIENT', 'MULTIPLE_CHOICE', 'NEGOTIATION', 'KENJOUGO', 'N1',
 '[{"text":"Subject: [Consultation] Regarding the delivery schedule for Project X. Body: We are deeply grateful for your continued partnership. We sincerely apologize for raising this matter, but we would like to humbly consult regarding the delivery schedule for Milestone Y. In order to ensure the quality meets the standards your esteemed company deserves, our team has determined that approximately 5 additional business days would allow us to deliver a more polished product. We deeply apologize for the inconvenience. We have prepared quality assurance measures for this period and would be honored to explain them at your convenience. We will spare no effort to deliver results that exceed your expectations.","textJp":"件名：【ご相談】プロジェクトXの納品スケジュールについて。本文：平素より格別のお引き立てを賜り、厚く御礼申し上げます。誠に恐れ入りますが、マイルストーンYの納品スケジュールについてご相談申し上げたく存じます。御社にふさわしい品質を確保するため、チームにて検討いたしましたところ、あと5営業日ほどお時間を頂戴できますと、より完成度の高い成果物をお届けできると判断いたしました。ご迷惑をおかけし大変申し訳ございません。この期間の品質保証施策をご用意しておりますので、ご都合のよろしい時にご説明の機会を賜れますと幸甚に存じます。ご期待を超える成果をお届けできるよう、全力を尽くしてまいります。","isOptimal":true,"culturalScore":98,"feedbackIfChosen":"Masterful business Japanese. Frames the delay as quality improvement for the client, uses highest kenjougo (賜る, 存じます, 幸甚), takes full responsibility, offers to explain in person, and commits to exceeding expectations. The quality framing turns a negative into a positive."},{"text":"Subject: Deadline change. Body: Hello, we need 5 more days for the project. We ran into some issues. Sorry for the trouble. We will get it done as soon as possible. Thank you for understanding.","textJp":"件名：納期変更。本文：お世話になっております。プロジェクトにあと5日必要です。いくつか問題が発生しました。ご迷惑をおかけして申し訳ありません。できるだけ早く完了します。ご理解のほどよろしくお願いします。","isOptimal":false,"culturalScore":25,"feedbackIfChosen":"Severely insufficient for communicating with a 取締役 of a client company. Too casual, missing proper kenjougo, no quality framing, no concrete plan, and ''thank you for understanding'' presumes agreement before it is given — culturally inappropriate."},{"text":"Subject: [Important] Delivery delay notification. Body: We regret to inform you that the deadline will be pushed back by 5 days due to technical difficulties. We apologize for any inconvenience caused. The new delivery date will be [date]. Please adjust your schedule accordingly.","textJp":"件名：【重要】納品遅延のお知らせ。本文：技術的な問題により、納期が5日間延長されることをお知らせいたします。ご迷惑をおかけして申し訳ございません。新しい納品日は○月○日です。スケジュールの調整をお願いいたします。","isOptimal":false,"culturalScore":30,"feedbackIfChosen":"Major issues: (1) framing as a notification rather than a consultation (you cannot dictate to a client), (2) ''adjust your schedule accordingly'' is presumptuous and potentially offensive to a 取締役, (3) no quality framing or solution presentation. This reads as an internal memo, not a client communication."}]',
 NULL, NULL,
 '["格別のお引き立てを賜り", "ご相談申し上げたく存じます", "お時間を頂戴できますと", "幸甚に存じます", "全力を尽くしてまいります"]',
 '["遅れます", "お知らせいたします（一方的）", "スケジュール調整をお願い", "問題が発生", "理解してください"]',
 'When requesting deadline extensions from clients, NEVER frame it as a notification — always as a consultation (ご相談). Frame the extra time as benefiting their quality standards. Use the highest register of kenjougo for 取締役-level communication. Always offer an in-person explanation for serious matters.',
 90, true);
