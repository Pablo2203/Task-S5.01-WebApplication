-- V4__manual_flow_changes.sql

-- 1) Quitar columnas de documento y consentimiento
ALTER TABLE medical_appointments
DROP COLUMN document_type,
  DROP COLUMN document_number,
  DROP COLUMN privacy_consent;

-- 2) Permitir null en campos que no se conocen en REQUESTED
ALTER TABLE medical_appointments
    MODIFY COLUMN professional_id BIGINT NULL,
    MODIFY COLUMN starts_at DATETIME NULL;

-- 3) Agregar tipo de cobertura del paciente
ALTER TABLE medical_appointments
    ADD COLUMN coverage_type VARCHAR(20) NULL AFTER phone;

-- (Opcional recomendado) Índice por estado para listados de solicitudes
-- CREATE INDEX idx_app_status ON medical_appointments (status);
