package com.barbearia.agendamento.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        // Ignora rotas públicas
        String path = request.getRequestURI();
        if (path.startsWith("/barbearia/login") || 
            path.startsWith("/barbearia/cadastro") ||
            path.startsWith("/barbearia/api/auth") ||
            path.startsWith("/oauth2") ||
            path.startsWith("/login") ||
            path.startsWith("/styles") ||
            path.startsWith("/script") ||
            path.startsWith("/imagens") ||
            path.startsWith("/css") ||
            path.startsWith("/js")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        // Verifica se há token no header
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);
        
        try {
            userEmail = jwtService.extractUsername(jwt);

            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

                if (jwtService.isTokenValid(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    
                    // Adiciona atributos à requisição para uso nos controllers
                    request.setAttribute("userId", jwtService.extractUserId(jwt));
                    request.setAttribute("permissao", jwtService.extractPermissao(jwt));
                    request.setAttribute("nome", jwtService.extractNome(jwt));
                }
            }
        } catch (Exception e) {
            // Token inválido - não autentica
            logger.warn("JWT inválido: " + e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}
