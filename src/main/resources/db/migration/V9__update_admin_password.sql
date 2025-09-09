-- Update admin password and ensure role/enabled
UPDATE users
SET password_hash = '$2b$10$ln6NXNmGR08v8JN1SVRX1OuszDVnLX0pWBiRK7WrNGtcTF4xgA9ba',
    role = 'ADMIN',
    enabled = 1
WHERE username = 'admin';

