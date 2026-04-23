package com.barbearia.agendamento.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Autowired
    private OAuth2LoginSuccessHandler successHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(

                                "/",
                                "/barbearia/login",
                                "/barbearia/cadastro",
                                "/barbearia/loginadm",

                                // permitir POST dos formulários
                                "/barbearia/**",

                                // oauth
                                "/oauth2/**",
                                "/login/**",

                                // estáticos
                                "/styles/**",
                                "/script/**",
                                "/imagens/**",
                                "/css/**",
                                "/js/**",
                                "/images/**"

                        ).permitAll()

                        .anyRequest().authenticated()
                )

                // NÃO usar formLogin
                .formLogin(form -> form.disable())

                .oauth2Login(oauth -> oauth
                        .loginPage("/barbearia/login")
                        .successHandler(successHandler)
                )

                .logout(logout -> logout
                        .logoutSuccessUrl("/barbearia/login")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                );

        return http.build();
    }
}