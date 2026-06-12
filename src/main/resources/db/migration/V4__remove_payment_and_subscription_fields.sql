ALTER TABLE users
DROP COLUMN pagamento,
DROP COLUMN data_expiracao,
DROP COLUMN data_criacao_conta;

ALTER TABLE profiles DROP COLUMN IF EXISTS nome;

TRUNCATE TABLE profiles CASCADE;