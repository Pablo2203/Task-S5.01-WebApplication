CREATE TABLE IF NOT EXISTS virtual_assistants (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    mood VARCHAR(20) NOT NULL,
    interaction_level INT DEFAULT 0,
    owner_professional_id BIGINT NULL,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    version BIGINT NULL
    );

CREATE INDEX idx_virtual_assistants_owner ON virtual_assistants (owner_professional_id);
