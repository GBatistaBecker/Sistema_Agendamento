CREATE TABLE IF NOT EXISTS tbl_clientes (
    id_cliente SERIAL PRIMARY KEY,
    nome_cliente VARCHAR(80) NOT NULL,
    telefone_cliente VARCHAR(15) NOT NULL UNIQUE,
    email_cliente VARCHAR(80) NOT NULL UNIQUE
    );