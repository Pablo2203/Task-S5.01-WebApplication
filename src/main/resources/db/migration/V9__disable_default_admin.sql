-- Disable seeded admin account by default; re-enable via admin approval flow
UPDATE users SET enabled = 0 WHERE username = 'admin';

