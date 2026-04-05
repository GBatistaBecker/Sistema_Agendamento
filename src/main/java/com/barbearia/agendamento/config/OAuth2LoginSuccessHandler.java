package com.barbearia.agendamento.config;

import com.barbearia.agendamento.model.Cliente;
import com.barbearia.agendamento.repository.ClienteRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuth2LoginSuccessHandler implements org.springframework.security.web.authentication.AuthenticationSuccessHandler {

    @Autowired
    private ClienteRepository clienteRepository;

    @Override
    public void onAuthenticationSuccess(
            jakarta.servlet.http.HttpServletRequest request,
            jakarta.servlet.http.HttpServletResponse response,
            Authentication authentication) throws IOException {

        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;

        String email = token.getPrincipal().getAttribute("email");
        String nome = token.getPrincipal().getAttribute("name");

        Cliente cliente = clienteRepository.findByEmailCliente(email)
                .orElseGet(() -> {
                    Cliente novo = new Cliente();
                    novo.setNomeCliente(nome);
                    novo.setEmailCliente(email);
                    novo.setTelefoneCliente("00000000000"); // placeholder
                    return clienteRepository.save(novo);
                });

        HttpSession session = request.getSession();
        session.setAttribute("clienteLogado", cliente);

        response.sendRedirect("/barbearia/servicos");
    }
}