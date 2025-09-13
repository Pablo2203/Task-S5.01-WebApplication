-- Seed professional user Virginia San Joaquín and profile with PSICOLOGIA
INSERT INTO users (username, email, password_hash, role, enabled, created_at)
SELECT 'virginia.sj', 'virginia@example.com', '$2b$10$ln6NXNmGR08v8JN1SVRX1OuszDVnLX0pWBiRK7WrNGtcTF4xgA9ba', 'PROFESSIONAL', 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM users u WHERE u.username = 'virginia.sj');

INSERT INTO professional_profiles (user_id, first_name, last_name, studies, specialty, bio, photo_url, updated_at)
SELECT u.id, 'Virginia', 'San Joaquín', 'Lic. en Psicología (UBA)', 'PSICOLOGIA', '', NULL, NOW()
FROM users u
WHERE u.username = 'virginia.sj'
  AND NOT EXISTS (SELECT 1 FROM professional_profiles p WHERE p.user_id = u.id);

