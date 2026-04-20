-- insere USER (id = 1)
INSERT INTO tbl_permissoes (tipo_permissao)
SELECT 'USER'
    WHERE NOT EXISTS (
    SELECT 1 FROM tbl_permissoes WHERE tipo_permissao = 'USER'
);

-- insere ADMIN (id = 2)
INSERT INTO tbl_permissoes (tipo_permissao)
SELECT 'ADMIN'
    WHERE NOT EXISTS (
    SELECT 1 FROM tbl_permissoes WHERE tipo_permissao = 'ADMIN'
);