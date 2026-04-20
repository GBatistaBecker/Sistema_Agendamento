-- cria funcionario
INSERT INTO tbl_funcionarios (
    nome_funcionario,
    funcao_funcionario,
    email_funcionario,
    telefone_funcionario
)
VALUES (
           'Arthur',
           'Administrador',
           'arthur@gmail.com',
           '(11) 99999-9999'
       );

-- cria usuario vinculado ao funcionario criado
INSERT INTO tbl_usuarios (
    email_usuario,
    senha_usuario,
    id_permissao,
    id_funcionario
)
VALUES (
           'arthur@gmail.com',
           'Admin@123',
           2,
           (SELECT id_funcionario
            FROM tbl_funcionarios
            WHERE email_funcionario = 'arthur@gmail.com'
               LIMIT 1)
    );