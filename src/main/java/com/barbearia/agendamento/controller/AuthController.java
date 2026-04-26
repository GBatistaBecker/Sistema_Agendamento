package com.barbearia.agendamento.controller;

import com.barbearia.agendamento.config.JwtService;
import com.barbearia.agendamento.model.Usuario;
import com.barbearia.agendamento.repository.UsuarioRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/barbearia/api/auth")
@Tag(name = "Autenticação JWT", description = "Endpoints de autenticação com JWT")
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Operation(summary = "Login com JWT", description = "Autentica usuário e retorna token JWT")
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmailUsuario(request.getEmail());

        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(401).body(Map.of("error", "Usuário não encontrado"));
        }

        Usuario usuario = usuarioOpt.get();

        // Verifica senha (para login normal, não OAuth)
        if (!usuario.getSenhaUsuario().equals("GOOGLE_LOGIN") && 
            !passwordEncoder.matches(request.getSenha(), usuario.getSenhaUsuario())) {
            return ResponseEntity.status(401).body(Map.of("error", "Senha incorreta"));
        }

        // Gera token JWT
        String token = jwtService.generateToken(
                usuario.getEmailUsuario(),
                usuario.getIdUsuario(),
                usuario.getPermissao().getTipoPermissao().name(),
                usuario.getCliente() != null ? usuario.getCliente().getNomeCliente() : 
                    (usuario.getFuncionario() != null ? usuario.getFuncionario().getNomeFuncionario() : "Admin")
        );

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("tokenType", "Bearer");
        response.put("email", usuario.getEmailUsuario());
        response.put("nome", usuario.getCliente() != null ? usuario.getCliente().getNomeCliente() : 
            (usuario.getFuncionario() != null ? usuario.getFuncionario().getNomeFuncionario() : "Admin"));
        response.put("permissao", usuario.getPermissao().getTipoPermissao().name());
        response.put("userId", usuario.getIdUsuario());

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Validar token", description = "Verifica se o token JWT é válido")
    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body(Map.of("valid", false, "error", "Token não fornecido"));
        }

        String token = authHeader.substring(7);
        try {
            String email = jwtService.extractUsername(token);
            boolean isValid = !jwtService.isTokenExpired(token);
            
            Map<String, Object> response = new HashMap<>();
            response.put("valid", isValid);
            response.put("email", email);
            response.put("userId", jwtService.extractUserId(token));
            response.put("permissao", jwtService.extractPermissao(token));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("valid", false, "error", e.getMessage()));
        }
    }

    @Operation(summary = "Obter usuário atual", description = "Retorna informações do usuário logado pelo token")
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body(Map.of("error", "Token não fornecido"));
        }

        String token = authHeader.substring(7);
        try {
            String email = jwtService.extractUsername(token);
            Optional<Usuario> usuarioOpt = usuarioRepository.findByEmailUsuario(email);

            if (usuarioOpt.isEmpty()) {
                return ResponseEntity.status(404).body(Map.of("error", "Usuário não encontrado"));
            }

            Usuario usuario = usuarioOpt.get();
            Map<String, Object> response = new HashMap<>();
            response.put("id", usuario.getIdUsuario());
            response.put("email", usuario.getEmailUsuario());
            response.put("permissao", usuario.getPermissao().getTipoPermissao().name());
            response.put("nome", usuario.getCliente() != null ? usuario.getCliente().getNomeCliente() : 
                (usuario.getFuncionario() != null ? usuario.getFuncionario().getNomeFuncionario() : "Admin"));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("error", e.getMessage()));
        }
    }

    // DTO interno
    public static class LoginRequest {
        private String email;
        private String senha;

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getSenha() { return senha; }
        public void setSenha(String senha) { this.senha = senha; }
    }
}
