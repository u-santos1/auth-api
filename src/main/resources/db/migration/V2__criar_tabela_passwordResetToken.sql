CREATE TABLE IF NOT EXISTS tb_password_reset_tokens (
    id BIGSERIAL PRIMARY KEY,
    token VARCHAR(255) NOT NULL UNIQUE,
    data_expiracao TIMESTAMP NOT NULL,
    usuario_id BIGINT NOT NULL,

    CONSTRAINT fk_reset_token_usuario FOREIGN KEY (usuario_id) REFERENCES tb_usuarios (id) ON DELETE CASCADE
);