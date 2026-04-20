CREATE TABLE IF NOT EXISTS tbl_agendamento (
    id_agendamento SERIAL PRIMARY KEY,

    id_cliente INTEGER NOT NULL,
    id_funcionario INTEGER,
    id_corte INTEGER NOT NULL,

    data_agendamento DATE,
    hora_agendamento TIME,

    status_agendamento VARCHAR(20) NOT NULL DEFAULT 'Agendado',

    observacao_agendamento TEXT,
    forma_pagamento VARCHAR(30),

    CONSTRAINT fk_agendamento_cliente
    FOREIGN KEY (id_cliente)
    REFERENCES tbl_clientes(id_cliente),

    CONSTRAINT fk_agendamento_funcionario
    FOREIGN KEY (id_funcionario)
    REFERENCES tbl_funcionarios(id_funcionario),

    CONSTRAINT fk_agendamento_servico
    FOREIGN KEY (id_corte)
    REFERENCES tbl_servicos(id_corte)
    );