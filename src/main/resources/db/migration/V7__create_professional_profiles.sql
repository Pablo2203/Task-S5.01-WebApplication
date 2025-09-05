CREATE TABLE IF NOT EXISTS professional_profiles (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL UNIQUE,
    first_name VARCHAR(100) NULL,
    last_name VARCHAR(100) NULL,
    studies VARCHAR(255) NULL,
    specialty VARCHAR(50) NULL,
    bio TEXT NULL,
    photo_url VARCHAR(255) NULL,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    version BIGINT NULL,
    CONSTRAINT fk_prof_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE INDEX idx_prof_user ON professional_profiles (user_id);

