INSERT INTO tbl_servicos (id_corte, duracao_corte, nome_corte, valor_corte)
SELECT 1, 30, 'Corte Tradicional', 30.00
    WHERE NOT EXISTS (
    SELECT 1 FROM tbl_servicos WHERE id_corte = 1
);

INSERT INTO tbl_servicos (id_corte, duracao_corte, nome_corte, valor_corte)
SELECT 2, 30, 'Corte Degradê', 30.00
    WHERE NOT EXISTS (
    SELECT 1 FROM tbl_servicos WHERE id_corte = 2
);

INSERT INTO tbl_servicos (id_corte, duracao_corte, nome_corte, valor_corte)
SELECT 3, 30, 'Barba Completa', 25.00
    WHERE NOT EXISTS (
    SELECT 1 FROM tbl_servicos WHERE id_corte = 3
);

INSERT INTO tbl_servicos (id_corte, duracao_corte, nome_corte, valor_corte)
SELECT 4, 30, 'Degradê + Barba', 50.00
    WHERE NOT EXISTS (
    SELECT 1 FROM tbl_servicos WHERE id_corte = 4
);