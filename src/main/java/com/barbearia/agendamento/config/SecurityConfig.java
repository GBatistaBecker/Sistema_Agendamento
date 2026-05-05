package com.barbearia.agendamento.config;

import com.barbearia.agendamento.service.UsuarioDetailsService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity // habilita @PreAuthorize nos controllers
public class SecurityConfig {

    @Autowired
    private OAuth2LoginSuccessHandler successHandler;

    @Autowired
    private JwtFilter jwtFilter;

    @Autowired
    private UsuarioDetailsService usuarioDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            if (request.getRequestURI().startsWith("/api")) {
                                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                                response.setContentType("application/json");
                                response.getWriter().write("{\"erro\": \"Não autorizado\"}");
                            } else {
                                // Para páginas web, redireciona para login
                                response.sendRedirect("/barbearia/login");
                            }
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            if (request.getRequestURI().startsWith("/api")) {
                                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                                response.setContentType("application/json");
                                response.getWriter().write("{\"erro\": \"Acesso negado\"}");
                            } else {
                                response.sendRedirect("/barbearia/login");
                            }
                        })
                )

                .authorizeHttpRequests(auth -> auth
                        // Rotas públicas — qualquer um acessa
                        .requestMatchers(
                                "/",
                                "/barbearia/login",
                                "/barbearia/cadastro",
                                "/barbearia/loginadm",
                                "/barbearia/logout",
                                "/barbearia/horarios-ocupados",
                                "/oauth2/**", "/login/**",
                                "/styles/**", "/script/**", "/imagens/**",
                                "/css/**", "/js/**", "/images/**",
                                "/api/auth/**"
                        ).permitAll()

                        // Rotas de API protegidas por JWT
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/**").authenticated()

                        // Rotas admin — só ADMIN acessa
                        .requestMatchers("/barbearia/admin/**", "/barbearia/relatorio/**")
                        .hasRole("ADMIN")

                        // Demais rotas web — exigem login
                        .requestMatchers("/barbearia/**").authenticated()

                        .anyRequest().authenticated()
                )

                .formLogin(form -> form.disable())

                .oauth2Login(oauth -> oauth
                        .loginPage("/barbearia/login")
                        .successHandler(successHandler)
                )

                .logout(logout -> logout
                        .logoutSuccessUrl("/barbearia/login")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                )

                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        var provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(usuarioDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }
}