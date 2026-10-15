-- =============================================
-- Esquema PostgreSQL - Gestión de Usuarios
--
-- Para Supabase: SQL Editor -> New query -> pegar esto -> Run.
-- O por linea de comandos:
--   psql "postgresql://<user>:<pass>@<host>:5432/postgres" -f schema-postgres.sql
--
-- Diferencias con el schema MySQL (schema.sql), que se mantiene para el compose local:
--   - PostgreSQL no tiene ENUM: se usa VARCHAR + CHECK
--   - No hay ENGINE=InnoDB ni CHARACTER SET: la codificacion se fija al crear la base
--   - ON UPDATE CURRENT_TIMESTAMP no existe: lo reemplaza un trigger
--   - No se hace CREATE DATABASE ni USE: en Supabase la base ya existe
-- =============================================

CREATE TABLE IF NOT EXISTS users (
    id          VARCHAR(36)  NOT NULL PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    email       VARCHAR(150) NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    role        VARCHAR(20)  NOT NULL DEFAULT 'MEMBER'
                            CONSTRAINT chk_users_role
                            CHECK (role IN ('ADMIN', 'MEMBER', 'REVIEWER')),
    status      VARCHAR(20)  NOT NULL DEFAULT 'PENDING'
                            CONSTRAINT chk_users_status
                            CHECK (status IN ('ACTIVE', 'INACTIVE', 'PENDING', 'BLOCKED')),
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- MySQL resolvia esto con ON UPDATE CURRENT_TIMESTAMP. En PostgreSQL hace falta un trigger.
CREATE OR REPLACE FUNCTION set_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_users_updated_at ON users;

CREATE TRIGGER trg_users_updated_at
    BEFORE UPDATE ON users
    FOR EACH ROW
    EXECUTE FUNCTION set_updated_at();

-- No se siembra ningun usuario por defecto, y por que.
--
-- Antes este script insertaba un admin@example.com con la contrasena "Admin1234!", pero el hash
-- era un placeholder ($2a$12$placeholder...): BCrypt exige exactamente 60 caracteres y este tenia
-- 47, asi que verify() devolvia false siempre y la cuenta no podia iniciar sesion. No era un
-- fallo de seguridad (fallaba cerrado), sino una cuenta muerta y un comentario engañoso.
--
-- Sembrar un admin con contrasena conocida si es un riesgo: en un despliegue nuevo ese par
-- usuario/contrasena existe en produccion y es publico, porque esta en el repositorio.
--
-- Para crear el primer administrador, registra un usuario por la API con role=ADMIN:
--
--   POST /api/users
--   { "name": "Administrador", "email": "tu@correo.com",
--     "password": "una contrasena larga y unica", "role": "ADMIN" }
--
-- La API hashea la contrasena con BCrypt (coste 12); nunca escribas el hash a mano aqui.