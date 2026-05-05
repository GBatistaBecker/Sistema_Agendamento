package com.barbearia.agendamento.controller;

import com.barbearia.agendamento.config.JwtUtil;
import com.barbearia.agendamento.model.Usuario;
import com.barbearia.agendamento.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {

        String email = body.get("email");
        String senha = body.get("senha");

        Optional<Usuario> optUsuario = usuarioRepository.findByEmailUsuario(email);

        if (optUsuario.isEmpty()) {
            return ResponseEntity.status(401).body(Map.of("erro", "Usuário não encontrado"));
        }

        Usuario usuario = optUsuario.get();

        // Verificação senha (com criptografia)
        if (!passwordEncoder.matches(senha, usuario.getSenhaUsuario())) {
            return ResponseEntity.status(401).body(Map.of("erro", "Senha inválida"));
        }

        String role = usuario.getPermissao().getTipoPermissao().name(); // "ADMIN" ou "USER"
        String token = jwtUtil.gerarToken(email, role);

        return ResponseEntity.ok(Map.of("token", token, "role", role));
    }
}
