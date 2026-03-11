package com.curriculovt.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private SecurityFilter securityFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/login", "/auth/forgot-password").permitAll()
                        .requestMatchers(HttpMethod.POST, "/users").permitAll()
                        .requestMatchers("/error").permitAll()
                        .requestMatchers(HttpMethod.POST, "/pagamentos/webhook").permitAll()
                        .requestMatchers(HttpMethod.GET, "/auth/me").hasAnyAuthority("ROLE_SUPER_ADMIN", "ROLE_COMMON")
                        .requestMatchers("/pagamentos/**").hasAnyAuthority("ROLE_SUPER_ADMIN", "ROLE_COMMON")
                        .requestMatchers("/users/metrics").hasAuthority("ROLE_SUPER_ADMIN")
                        .requestMatchers("/users/all").hasAuthority("ROLE_SUPER_ADMIN")

                        .requestMatchers(HttpMethod.GET, "/users/{id}").hasAnyAuthority("ROLE_SUPER_ADMIN", "ROLE_COMMON")
                        .requestMatchers(HttpMethod.PUT, "/users/{id}").hasAnyAuthority("ROLE_SUPER_ADMIN", "ROLE_COMMON")

                        .requestMatchers("/users", "/users/**").hasAuthority("ROLE_SUPER_ADMIN")

                        .requestMatchers(
                                "/profiles", "/profiles/**",
                                "/experiencias", "/experiencias/**",
                                "/formacoes", "/formacoes/**",
                                "/habilidades", "/habilidades/**",
                                "/idiomas", "/idiomas/**"
                        ).hasAnyAuthority("ROLE_SUPER_ADMIN", "ROLE_COMMON")

                        .anyRequest().authenticated()
                )
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(
                "http://localhost:5173",
                "http://31.97.22.121:5173",
                "https://curriculovt.com.br",
                "https://www.curriculovt.com.br"
        ));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Requested-With"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}