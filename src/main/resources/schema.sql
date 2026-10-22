-- =============================================
-- Script de creación de la base de datos
-- Gestión de Usuarios - Arquitectura Hexagonal
-- =============================================

CREATE DATABASE IF NOT EXISTS crud_usuarios
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE crud_usuarios;

CREATE TABLE IF NOT EXISTS users (
    id          VARCHAR(36)  NOT NULL PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    email       VARCHAR(150) NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    role        ENUM('ADMIN', 'MEMBER', 'REVIEWER') NOT NULL,
    status      ENUM('ACTIVE', 'INACTIVE', 'PENDING', 'BLOCKED') NOT NULL DEFAULT 'PENDING',
    created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

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
-- Ese registro es publico SOLO mientras la tabla este vacia. En cuanto existe un usuario,
-- POST /api/users exige un JWT con rol ADMIN (BootstrapCreateUserAuthorization). Asi el
-- arranque sigue funcionando pero nadie puede meter filas ni disparar correos de bienvenida
-- a direcciones arbitrarias, que agotarian la cuota de Brevo.
--
-- Los roles no se cambian desde fuera: PUT /api/users/{id} exige ADMIN, asi que un
-- REVIEWER solo puede listar y consultar.
--
-- La API hashea la contrasena con BCrypt (coste 12); nunca escribas el hash a mano aqui.

