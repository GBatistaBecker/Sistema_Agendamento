package com.barbearia.agendamento.config;

import com.barbearia.agendamento.model.Cliente;
import com.barbearia.agendamento.model.Permissao;
import com.barbearia.agendamento.model.Usuario;
import com.barbearia.agendamento.repository.ClienteRepository;
import com.barbearia.agendamento.repository.UsuarioRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public void onAuthenticationSuccess(
            jakarta.servlet.http.HttpServletRequest request,
            jakarta.servlet.http.HttpServletResponse response,
            Authentication authentication) throws IOException {

        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;

        String email = token.getPrincipal().getAttribute("email");
        String nome = token.getPrincipal().getAttribute("name");

        // =========================
        // BUSCA OU CRIA CLIENTE
        // =========================

        Cliente cliente = clienteRepository.findByEmailCliente(email)
                .orElseGet(() -> {
                    Cliente novoCliente = new Cliente();
                    novoCliente.setNomeCliente(nome);
                    novoCliente.setEmailCliente(email);
                    novoCliente.setTelefoneCliente("00000000000");
                    return clienteRepository.save(novoCliente);
                });

        // =========================
        // BUSCA OU CRIA USUARIO
        // =========================

        Usuario usuario = usuarioRepository.findByEmailUsuario(email)
                .orElseGet(() -> {

                    Usuario novoUsuario = new Usuario();
                    novoUsuario.setEmailUsuario(email);
                    novoUsuario.setSenhaUsuario("GOOGLE_LOGIN");
                    novoUsuario.setCliente(cliente);

                    Permissao permissao = new Permissao();
                    permissao.setIdPermissao(1); // Cliente

                    novoUsuario.setPermissao(permissao);

                    return usuarioRepository.save(novoUsuario);
                });

        // =========================
        // SESSAO PADRONIZADA
        // =========================

        HttpSession session = request.getSession();
        session.setAttribute("usuarioLogado", usuario);

        response.sendRedirect("/barbearia/servicos");
    }
}