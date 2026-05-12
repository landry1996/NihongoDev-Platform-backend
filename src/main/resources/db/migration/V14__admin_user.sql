-- Create admin user account
INSERT INTO users (id, first_name, last_name, email, password, role, japanese_level, objective, is_active, created_at, updated_at)
VALUES (
    uuid_generate_v4(),
    'Tchiengue',
    'Pierre Landry',
    'ltchiengue73@gmail.com',
    '$2a$12$nSA.la4XUPh98nCN.pEsyuJahbYCCQ4wJhcO/2.7w/tx8BQ//5VT.',
    'ADMIN',
    'BEGINNER',
    'Administration',
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
)
ON CONFLICT (email) DO NOTHING;
