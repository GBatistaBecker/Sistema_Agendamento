CREATE TABLE IF NOT EXISTS tbl_servicos (
    id_corte SERIAL PRIMARY KEY,
    nome_corte VARCHAR(50) NOT NULL,
    valor_corte DECIMAL(10,2) NOT NULL,
    duracao_corte BIGINT NOT NULL
    );