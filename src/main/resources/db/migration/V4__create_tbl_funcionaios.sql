CREATE TABLE IF NOT EXISTS tbl_funcionarios (
    id_funcionario SERIAL PRIMARY KEY,
    nome_funcionario VARCHAR(50) NOT NULL,
    funcao_funcionario VARCHAR(30) NOT NULL,
    email_funcionario VARCHAR(80) NOT NULL,
    telefone_funcionario VARCHAR(15) NOT NULL
    );