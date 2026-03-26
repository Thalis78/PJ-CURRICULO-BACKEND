CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    data_expiracao TIMESTAMP,
    senha_redefinida_por_email BOOLEAN NOT NULL DEFAULT FALSE,
    pagamento BOOLEAN NOT NULL DEFAULT FALSE
    );

CREATE TABLE IF NOT EXISTS user_audit_log (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT,
    acao VARCHAR(255),
    data_evento TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

CREATE TABLE IF NOT EXISTS profiles (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    nome_completo VARCHAR(100) NOT NULL,
    img TEXT,
    resumo VARCHAR(500),
    objetivo VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    telefone VARCHAR(15) NOT NULL,
    linkedin VARCHAR(255),
    estado VARCHAR(255) NOT NULL,
    CONSTRAINT fk_profiles_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS experiencias (
    id BIGSERIAL PRIMARY KEY,
    empresa VARCHAR(255) NOT NULL,
    cargo VARCHAR(255) NOT NULL,
    data_inicio DATE,
    data_fim DATE,
    atualmente BOOLEAN NOT NULL DEFAULT FALSE,
    descricao TEXT,
    profile_id BIGINT NOT NULL,
    CONSTRAINT fk_experiencias_profile FOREIGN KEY (profile_id) REFERENCES profiles (id) ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS formacoes (
    id BIGSERIAL PRIMARY KEY,
    instituicao VARCHAR(255) NOT NULL,
    curso VARCHAR(255) NOT NULL,
    tipo VARCHAR(100) NOT NULL,
    data_inicio DATE,
    data_fim DATE,
    atualmente BOOLEAN NOT NULL DEFAULT FALSE,
    profile_id BIGINT NOT NULL,
    CONSTRAINT fk_formacoes_profile FOREIGN KEY (profile_id) REFERENCES profiles (id) ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS habilidades (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(30) NOT NULL,
    profile_id BIGINT NOT NULL,
    CONSTRAINT fk_habilidades_profile FOREIGN KEY (profile_id) REFERENCES profiles (id) ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS idiomas (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    nivel VARCHAR(255) NOT NULL,
    profile_id BIGINT NOT NULL,
    CONSTRAINT fk_idiomas_profile FOREIGN KEY (profile_id) REFERENCES profiles (id) ON DELETE CASCADE
    );

CREATE INDEX IF NOT EXISTS idx_profiles_user_id ON profiles(user_id);
CREATE INDEX IF NOT EXISTS idx_experiencias_profile_id ON experiencias(profile_id);
CREATE INDEX IF NOT EXISTS idx_formacoes_profile_id ON formacoes(profile_id);