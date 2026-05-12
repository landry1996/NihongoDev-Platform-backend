-- V11__real_content_engine.sql

CREATE TABLE real_contents (
    id                          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title                       VARCHAR(300) NOT NULL,
    title_reading               VARCHAR(500),
    body                        TEXT NOT NULL,
    summary                     TEXT,
    source_url                  VARCHAR(1000),
    source                      VARCHAR(30) NOT NULL,
    domain                      VARCHAR(30) NOT NULL,
    difficulty                  VARCHAR(10),
    reading_difficulty          VARCHAR(20),
    annotations                 JSONB,
    tags                        JSONB,
    key_vocabulary              JSONB,
    word_count                  INTEGER NOT NULL DEFAULT 0,
    kanji_count                 INTEGER NOT NULL DEFAULT 0,
    estimated_reading_minutes   INTEGER NOT NULL DEFAULT 1,
    status                      VARCHAR(20) NOT NULL DEFAULT 'INGESTED',
    author_name                 VARCHAR(200),
    published_at                TIMESTAMP,
    ingested_at                 TIMESTAMP NOT NULL DEFAULT NOW(),
    annotated_at                TIMESTAMP
);

CREATE TABLE content_reading_sessions (
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id                 UUID NOT NULL REFERENCES users(id),
    content_id              UUID NOT NULL REFERENCES real_contents(id),
    reading_time_seconds    INTEGER NOT NULL DEFAULT 0,
    annotations_viewed      INTEGER NOT NULL DEFAULT 0,
    vocabulary_looked_up    INTEGER NOT NULL DEFAULT 0,
    comprehension_score     DOUBLE PRECISION NOT NULL DEFAULT 0,
    saved_vocabulary        JSONB,
    completed               BOOLEAN NOT NULL DEFAULT false,
    started_at              TIMESTAMP NOT NULL DEFAULT NOW(),
    completed_at            TIMESTAMP
);

CREATE TABLE user_content_preferences (
    id                          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id                     UUID NOT NULL UNIQUE REFERENCES users(id),
    preferred_domains           JSONB,
    current_level               VARCHAR(10),
    max_difficulty              VARCHAR(20),
    preferred_reading_minutes   INTEGER NOT NULL DEFAULT 10,
    preferred_sources           JSONB
);

-- Indexes
CREATE INDEX idx_real_contents_status ON real_contents(status);
CREATE INDEX idx_real_contents_domain ON real_contents(domain);
CREATE INDEX idx_real_contents_difficulty ON real_contents(reading_difficulty);
CREATE INDEX idx_real_contents_source_url ON real_contents(source_url);
CREATE INDEX idx_reading_sessions_user ON content_reading_sessions(user_id);
CREATE INDEX idx_reading_sessions_content ON content_reading_sessions(content_id);
CREATE INDEX idx_reading_sessions_user_content ON content_reading_sessions(user_id, content_id);

-- Seed: Sample real Japanese tech content
INSERT INTO real_contents (id, title, title_reading, body, source_url, source, domain, difficulty, reading_difficulty, annotations, tags, key_vocabulary, word_count, kanji_count, estimated_reading_minutes, status, author_name, published_at, ingested_at, annotated_at)
VALUES
(
    'a1b2c3d4-e5f6-7890-abcd-ef1234567890',
    'マイクロサービスにおけるAPI設計のベストプラクティス',
    'マイクロサービスにおけるAPIせっけいのベストプラクティス',
    'マイクロサービスアーキテクチャにおいて、API設計は非常に重要な役割を果たします。本記事では、実装における設計原則と保守性を高めるためのベストプラクティスを紹介します。

まず、RESTful APIの設計において重要なのは、リソースの命名規則です。URLパスにはケバブケースを使用し、クエリパラメータにはキャメルケースを使用することが推奨されます。

次に、エラーハンドリングについてです。適切なHTTPステータスコードを返すことに加えて、エラーレスポンスの本文には問題の詳細を含めるべきです。これにより、クライアント側でのデバッグが容易になります。

認証と認可に関しては、OAuth 2.0とJWTトークンの組み合わせが一般的です。マイクロサービス間の通信においては、サービスメッシュを活用した相互TLS認証が推奨されます。

最後に、APIバージョニングについてです。URLパスにバージョンを含める方法（/api/v1/）が最も一般的ですが、ヘッダーベースのバージョニングも選択肢の一つです。',
    'https://example.com/microservice-api-design',
    'TECH_BLOG',
    'WEB_DEVELOPMENT',
    'N2',
    'INTERMEDIATE',
    '[{"id":"11111111-1111-1111-1111-111111111111","startOffset":0,"endOffset":9,"surfaceForm":"マイクロサービス","reading":"マイクロサービス","meaning":"microservice architecture pattern","annotationType":"TECHNICAL_TERM","requiredLevel":"N3","grammarNote":null,"culturalNote":null},{"id":"22222222-2222-2222-2222-222222222222","startOffset":19,"endOffset":21,"surfaceForm":"設計","reading":"せっけい","meaning":"design/architecture","annotationType":"VOCABULARY","requiredLevel":"N3","grammarNote":null,"culturalNote":null},{"id":"33333333-3333-3333-3333-333333333333","startOffset":53,"endOffset":55,"surfaceForm":"実装","reading":"じっそう","meaning":"implementation","annotationType":"VOCABULARY","requiredLevel":"N3","grammarNote":null,"culturalNote":null},{"id":"44444444-4444-4444-4444-444444444444","startOffset":60,"endOffset":63,"surfaceForm":"保守性","reading":"ほしゅせい","meaning":"maintainability","annotationType":"VOCABULARY","requiredLevel":"N1","grammarNote":null,"culturalNote":null},{"id":"55555555-5555-5555-5555-555555555555","startOffset":14,"endOffset":18,"surfaceForm":"において","reading":"において","meaning":"in / at / regarding (formal)","annotationType":"GRAMMAR_PATTERN","requiredLevel":"N2","grammarNote":"Formal equivalent of で indicating location/context. Common in documentation.","culturalNote":null}]',
    '["api", "backend", "security"]',
    '["設計", "実装", "保守性", "認証", "推奨"]',
    430,
    85,
    3,
    'PUBLISHED',
    '田中太郎',
    NOW(),
    NOW(),
    NOW()
),
(
    'b2c3d4e5-f6a7-8901-bcde-f12345678901',
    'Dockerコンテナのセキュリティ対策ガイド',
    'Dockerコンテナのセキュリティたいさくガイド',
    'コンテナ技術の普及に伴い、セキュリティの重要性がますます高まっています。本ガイドでは、Docker環境における脆弱性対策について説明します。

1. イメージの最小化
不要なパッケージを含まないベースイメージを選択することが重要です。Alpine Linuxをベースにすることで、攻撃対象の面積を減らすことができます。

2. 非rootユーザーでの実行
コンテナ内のプロセスはrootユーザーで実行しないようにするべきです。Dockerfileで専用ユーザーを作成し、USERディレクティブで切り替えます。

3. シークレット管理
環境変数にシークレットを直接埋め込むことは避けるべきです。Docker SecretsやHashiCorp Vaultなどの専用ツールを活用しましょう。

4. ネットワークの分離
マイクロサービス間の通信は、必要最小限のネットワーク接続のみを許可する設計にするべきです。Docker Composeのネットワーク機能を使って、サービス間のアクセスを制御します。',
    'https://example.com/docker-security-guide',
    'TECH_BLOG',
    'CLOUD_INFRASTRUCTURE',
    'N2',
    'ADVANCED',
    '[{"id":"66666666-6666-6666-6666-666666666666","startOffset":0,"endOffset":4,"surfaceForm":"コンテナ","reading":"コンテナ","meaning":"container (Docker/OCI)","annotationType":"TECHNICAL_TERM","requiredLevel":"N3","grammarNote":null,"culturalNote":null},{"id":"77777777-7777-7777-7777-777777777777","startOffset":16,"endOffset":19,"surfaceForm":"脆弱性","reading":"ぜいじゃくせい","meaning":"vulnerability","annotationType":"VOCABULARY","requiredLevel":"N1","grammarNote":null,"culturalNote":null},{"id":"88888888-8888-8888-8888-888888888888","startOffset":9,"endOffset":13,"surfaceForm":"に伴い","reading":"にともない","meaning":"along with / as ~ accompanies","annotationType":"GRAMMAR_PATTERN","requiredLevel":"N2","grammarNote":"Indicates that something changes in conjunction with something else.","culturalNote":null}]',
    '["container", "security", "devops"]',
    '["脆弱性", "暗号化", "接続", "設計", "制御"]',
    380,
    72,
    3,
    'PUBLISHED',
    '佐藤花子',
    NOW(),
    NOW(),
    NOW()
),
(
    'c3d4e5f6-a7b8-9012-cdef-123456789012',
    'Spring Bootにおける非同期処理の実装パターン',
    'Spring Bootにおけるひどうきしょりのじっそうパターン',
    'Spring Bootアプリケーションにおいて、非同期処理は性能向上に欠かせない技術です。本記事では、@Asyncアノテーションの使い方から、CompletableFutureを活用した並行処理まで解説します。

基本的な非同期処理
@Asyncアノテーションを使用することで、メソッドを非同期に実行できます。ただし、@EnableAsyncの設定が必要です。

CompletableFutureの活用
複数の非同期処理の結果を組み合わせる場合、CompletableFutureが有効です。thenCombineやallOfを使って、依存関係のある処理を効率的に実行できます。

エラーハンドリング
非同期処理における例外処理は注意が必要です。AsyncUncaughtExceptionHandlerを実装して、未キャッチ例外を適切に処理しましょう。

スレッドプールの設定
デフォルトのスレッドプールは本番環境に適していません。TaskExecutorをカスタマイズして、アプリケーションの要件に合わせた設定を行います。',
    'https://example.com/spring-boot-async',
    'TECH_BLOG',
    'WEB_DEVELOPMENT',
    'N1',
    'ADVANCED',
    '[{"id":"99999999-9999-9999-9999-999999999999","startOffset":30,"endOffset":33,"surfaceForm":"非同期","reading":"ひどうき","meaning":"asynchronous","annotationType":"VOCABULARY","requiredLevel":"N1","grammarNote":null,"culturalNote":null},{"id":"aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa","startOffset":97,"endOffset":101,"surfaceForm":"並行処理","reading":"へいこうしょり","meaning":"parallel processing","annotationType":"KANJI_BREAKDOWN","requiredLevel":"N1","grammarNote":null,"culturalNote":null},{"id":"bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb","startOffset":50,"endOffset":54,"surfaceForm":"依存関係","reading":"いぞんかんけい","meaning":"dependency","annotationType":"VOCABULARY","requiredLevel":"N1","grammarNote":null,"culturalNote":null}]',
    '["backend", "api"]',
    '["非同期", "並行処理", "依存関係", "実装", "設定"]',
    350,
    68,
    2,
    'PUBLISHED',
    '山田健一',
    NOW(),
    NOW(),
    NOW()
);
