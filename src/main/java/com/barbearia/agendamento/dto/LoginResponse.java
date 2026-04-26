package com.barbearia.agendamento.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resposta de autenticação com JWT")
public class LoginResponse {
    
    @Schema(description = "Token JWT", example = "eyJhbGciOiJIUzI1NiIs...")
    private String token;
    
    @Schema(description = "Tipo do token", example = "Bearer")
    private String tokenType;
    
    @Schema(description = "Email do usuário", example = "joao@email.com")
    private String email;
    
    @Schema(description = "Nome do usuário", example = "João Silva")
    private String nome;
    
    @Schema(description = "Permissão do usuário", example = "CLIENTE")
    private String permissao;
    
    @Schema(description = "ID do usuário", example = "1")
    private Integer userId;

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getTokenType() { return tokenType; }
    public void setTokenType(String tokenType) { this.tokenType = tokenType; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getPermissao() { return permissao; }
    public void setPermissao(String permissao) { this.permissao = permissao; }
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
}
