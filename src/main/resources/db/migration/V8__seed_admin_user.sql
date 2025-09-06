-- Seed initial admin user (change password after first login)
INSERT INTO users (username, email, password_hash, role, enabled, created_at)
    VALUES ('admin', 'consultoriosyrigoyen@gmail.com', '$2a$10$6bCv2e8pG0QKX5X6zP2lPey3m.m8i8cQ9O/0d5ZJ2sB/v0VtF1oK6', 'ADMIN', 1, NOW())
ON DUPLICATE KEY UPDATE username=username;

