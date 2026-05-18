CREATE TABLE IF NOT EXISTS tb_usuarios (
    id          BIGSERIAL PRIMARY KEY,
    nome        VARCHAR(100) NOT NULL,
    email       VARCHAR(150) NOT NULL UNIQUE,
    senha       VARCHAR(255) NOT NULL,
    ativo       BOOLEAN NOT NULL DEFAULT TRUE,
    perfil      VARCHAR(20) NOT NULL,
    created_at  TIMESTAMP DEFAULT NOW()
);