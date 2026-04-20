CREATE TABLE IF NOT EXISTS tbl_log_acessos (
    id_log SERIAL PRIMARY KEY,

    id_usuario INTEGER NOT NULL,

    data_acesso DATE,
    hora_acesso TIME,

    tipo_acesso VARCHAR(20),
    ip_acesso VARCHAR(45),

    CONSTRAINT fk_log_usuario
    FOREIGN KEY (id_usuario)
    REFERENCES tbl_usuarios(id_usuario)
    );