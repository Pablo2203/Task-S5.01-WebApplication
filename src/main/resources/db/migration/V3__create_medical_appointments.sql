CREATE TABLE IF NOT EXISTS medical_appointments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    patient_id BIGINT NULL,
    professional_id BIGINT NOT NULL,
    specialty VARCHAR(50) NOT NULL,
    starts_at DATETIME NOT NULL,
    ends_at DATETIME NULL,
    status VARCHAR(20) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    document_type VARCHAR(20) NOT NULL,
    document_number VARCHAR(20) NOT NULL,
    email VARCHAR(150) NULL,
    phone VARCHAR(20) NULL,
    health_insurance VARCHAR(100) NULL,
    health_plan VARCHAR(50) NULL,
    affiliate_number VARCHAR(50) NULL,
    subject VARCHAR(255) NULL,
    message TEXT NULL,
    privacy_consent TINYINT(1) NOT NULL,
    created_at DATETIME NULL,
    updated_at DATETIME NULL,
    version BIGINT NULL
    );

-- Índices útiles
CREATE INDEX idx_app_professional_starts ON medical_appointments (professional_id, starts_at);
CREATE INDEX idx_app_patient ON medical_appointments (patient_id);
