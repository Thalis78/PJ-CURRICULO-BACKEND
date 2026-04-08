CREATE TABLE precos (
    id BIGSERIAL PRIMARY KEY,
    valor_base DECIMAL(10, 2) NOT NULL CHECK (valor_base > 0),
    percentual_desconto INTEGER NOT NULL DEFAULT 0 CHECK (percentual_desconto >= 0 AND percentual_desconto <= 100)
);

INSERT INTO precos (valor_base, percentual_desconto)
VALUES (10.99, 0);