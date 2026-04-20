CREATE TABLE IF NOT EXISTS tbl_tempo_atendimento (
    id_atendimento SERIAL PRIMARY KEY,
    id_agendamento INTEGER NOT NULL UNIQUE,

    hora_inicio TIME,
    hora_termino TIME,
    duracao_atendimento INTEGER,
    observacao_atendimento TEXT,

   CONSTRAINT fk_tempo_agendamento
   FOREIGN KEY (id_agendamento)
    REFERENCES tbl_agendamento(id_agendamento)
    );