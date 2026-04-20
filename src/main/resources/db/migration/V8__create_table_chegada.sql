CREATE TABLE IF NOT EXISTS tbl_chegada (
    id_chegada SERIAL PRIMARY KEY,
    id_agendamento INTEGER NOT NULL UNIQUE,

    hora_chegada TIME,
    hora_saida TIME,
    status_chegada VARCHAR(30),

    CONSTRAINT fk_chegada_agendamento
    FOREIGN KEY (id_agendamento)
    REFERENCES tbl_agendamento(id_agendamento)
    );