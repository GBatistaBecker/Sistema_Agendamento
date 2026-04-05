package com.barbearia.agendamento.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/",
                                "/barbearia/login",
                                "/oauth2/**",
                                "/login/**",

                                // 🔥 LIBERAR ESTÁTICOS (IMPORTANTE)
                                "/styles/**",
                                "/script/**",
                                "/imagens/**",

                                // (opcional, mas bom garantir)
                                "/css/**",
                                "/js/**",
                                "/images/**"
                        ).permitAll()

                        .anyRequest().authenticated()
                )

                .oauth2Login(oauth -> oauth
                        .loginPage("/barbearia/login")
                        .defaultSuccessUrl("/barbearia/servicos", true)
                )

                .logout(logout -> logout
                        .logoutSuccessUrl("/")
                );

        return http.build();
    }
}