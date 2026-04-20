CREATE TABLE IF NOT EXISTS tbl_funcionario_servico (
    id_funcionario INTEGER NOT NULL,
    id_corte INTEGER NOT NULL,

    PRIMARY KEY (id_funcionario, id_corte),

    CONSTRAINT fk_funcionario_servico_funcionario
    FOREIGN KEY (id_funcionario)
    REFERENCES tbl_funcionarios(id_funcionario),

    CONSTRAINT fk_funcionario_servico_servico
    FOREIGN KEY (id_corte)
    REFERENCES tbl_servicos(id_corte)
    );