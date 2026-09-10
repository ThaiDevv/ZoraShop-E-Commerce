ALTER TABLE users
DROP COLUMN created_at,
    DROP COLUMN updated_at,
    ADD COLUMN date_of_birth DATE,
    ADD COLUMN sex VARCHAR(20);