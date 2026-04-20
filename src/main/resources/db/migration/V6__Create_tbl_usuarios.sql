CREATE TABLE IF NOT EXISTS tbl_usuarios (
    id_usuario SERIAL PRIMARY KEY,
    email_usuario VARCHAR(80) NOT NULL UNIQUE,
    senha_usuario VARCHAR(255) NOT NULL,

    id_permissao INTEGER NOT NULL,
    id_cliente INTEGER,
    id_funcionario INTEGER,

    CONSTRAINT fk_usuario_permissao
    FOREIGN KEY (id_permissao)
    REFERENCES tbl_permissoes(id_permissao),

    CONSTRAINT fk_usuario_cliente
    FOREIGN KEY (id_cliente)
    REFERENCES tbl_clientes(id_cliente),

    CONSTRAINT fk_usuario_funcionario
    FOREIGN KEY (id_funcionario)
    REFERENCES tbl_funcionarios(id_funcionario)
    );