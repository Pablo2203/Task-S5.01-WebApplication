ALTER TABLE medical_appointments
    ADD COLUMN reminder_sent_at DATETIME NULL AFTER updated_at;

