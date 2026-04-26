# 🔐 Configuração JWT - Guia Completo

Este guia explica como configurar e testar a autenticação JWT no sistema.

---

## 📋 PASSOS DE CONFIGURAÇÃO

### **1. Adicionar Dependências (pom.xml)**

Adicione antes de `</dependencies>`:

```xml
<!-- JWT -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.5</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.12.5</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.12.5</version>
</dependency>
```

### **2. Configurar application.properties**

Adicione no final do arquivo:

```properties
# JWT Configuration
jwt.secret=meu-segredo-super-seguro-para-jwt-32chars!!
jwt.expiration=86400000
```

**Importante**: A chave secreta deve ter pelo menos 32 caracteres para segurança HS256.

### **3. Substituir SecurityConfig**

**Opção A** (Recomendada): Renomeie `SecurityConfigJwt.java` para `SecurityConfig.java`

OU

**Opção B**: Copie o conteúdo de `SecurityConfigJwt.java` para `SecurityConfig.java`

### **4. Atualizar OAuth2 Handler (Opcional)**

Para suportar JWT no login Google, modifique `OAuth2LoginSuccessHandler.java` para gerar token JWT após login:

```java
@Autowired
private JwtService jwtService;

// Após salvar usuário, gere o token:
String jwtToken = jwtService.generateToken(
    usuario.getEmailUsuario(),
    usuario.getIdUsuario(),
    usuario.getPermissao().getTipoPermissao().name(),
    usuario.getCliente().getNomeCliente()
);

// Retorne o token (salve no cookie ou redirecione com token na URL)
response.addCookie(new Cookie("jwt_token", jwtToken));
```

---

## 🧪 COMO TESTAR

### **Teste 1: Login via API (cURL ou Postman)**

#### Endpoint: `POST /barbearia/api/auth/login`

**Requisição:**
```json
{
  "email": "usuario@email.com",
  "senha": "senha123"
}
```

**cURL:**
```bash
curl -X POST http://localhost:8080/barbearia/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"usuario@email.com","senha":"senha123"}'
```

**Resposta esperada:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIs...",
  "tokenType": "Bearer",
  "email": "usuario@email.com",
  "nome": "João Silva",
  "permissao": "CLIENTE",
  "userId": 1
}
```

### **Teste 2: Acessar endpoint protegido**

#### Endpoint: `GET /barbearia/api/auth/me`

**cURL:**
```bash
curl -X GET http://localhost:8080/barbearia/api/auth/me \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIs..."
```

**Resposta esperada:**
```json
{
  "id": 1,
  "email": "usuario@email.com",
  "permissao": "CLIENTE",
  "nome": "João Silva"
}
```

### **Teste 3: Validar token**

#### Endpoint: `GET /barbearia/api/auth/validate`

**cURL:**
```bash
curl -X GET http://localhost:8080/barbearia/api/auth/validate \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIs..."
```

**Resposta esperada:**
```json
{
  "valid": true,
  "email": "usuario@email.com",
  "userId": 1,
  "permissao": "CLIENTE"
}
```

---

## 🔧 Teste com Swagger UI

Acesse: `http://localhost:8080/swagger-ui/index.html`

1. Execute `POST /barbearia/api/auth/login` para obter token
2. Clique em **"Authorize"** no topo da página
3. Digite: `Bearer eyJhbGciOiJIUzI1NiIs...` (seu token)
4. Agora todos os endpoints protegidos funcionarão

---

## 📝 Fluxo de Autenticação JWT

```
┌─────────────┐     POST /login      ┌─────────────┐
│   Cliente   │ ───────────────────> │   Servidor  │
│  (Browser)  │  {email, senha}       │             │
└─────────────┘                       └─────────────┘
                                            │
                                            │ Gera JWT
                                            ▼
                                     ┌─────────────┐
                                     │    Token    │
                                     │   JWT       │
                                     └─────────────┘
                                            │
                                            │ Retorna token
                                            ▼
┌─────────────┐     Token JWT         ┌─────────────┐
│   Cliente   │ <───────────────────  │   Servidor  │
│  (Browser)  │                       │             │
└─────────────┘                       └─────────────┘
       │
       │ Guarda token (localStorage/cookie)
       ▼
┌─────────────┐
│  Requisição │  Authorization: Bearer <token>
│  Protegida  │ ───────────────────────────────>
└─────────────┘
```

---

## ⚠️ IMPORTANTE - MIGRAÇÃO DE SENHAS

O sistema agora usa **BCrypt** para senhas. As senhas existentes precisam ser atualizadas:

### Opção 1: Resetar senha no banco
```sql
-- Exemplo: Atualizar senha para "123456" (criptografada)
UPDATE tbl_usuarios SET senha_usuario = '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjXdP0o0m/.gJ5q2LW5fKw0k6Qq3lXu' 
WHERE id_usuario = 1;
```

### Opção 2: Script de migração Java
Crie um endpoint temporário para re-criptografar todas as senhas.

---

## 🛡️ Segurança

- **Nunca** commit a `jwt.secret` real
- Use variáveis de ambiente em produção:
  ```properties
  jwt.secret=${JWT_SECRET:chave-padrao}
  ```
- Tokens expiram em 24h (configurável via `jwt.expiration`)

---

## 🐛 Troubleshooting

### Erro: "Token inválido" ou "Expired JWT"
- Verifique se `jwt.secret` tem 32+ caracteres
- Verifique se o token não expirou

### Erro: "Usuário não encontrado"
- Verifique se email existe no banco
- Verifique se permissão está preenchida

### Erro: "Senha incorreta"
- Senha precisa estar em BCrypt (não texto plano)
- Use o PasswordEncoder para gerar hash de teste

---

## 📁 Arquivos Criados

1. `config/JwtService.java` - Gera/valida tokens
2. `config/JwtAuthenticationFilter.java` - Filtro de requisições
3. `config/UserDetailsServiceImpl.java` - Carrega usuário
4. `config/SecurityConfigJwt.java` - Configuração de segurança
5. `controller/AuthController.java` - Endpoints de auth
6. `dto/LoginRequest.java` - DTO de login
7. `dto/LoginResponse.java` - DTO de resposta

---

## ✅ Checklist Final

- [ ] Dependências adicionadas no pom.xml
- [ ] `jwt.secret` configurado no application.properties
- [ ] SecurityConfig atualizado com JWT
- [ ] Banco de dados com senhas em BCrypt
- [ ] Teste de login funcionando
- [ ] Teste de endpoint protegido funcionando
