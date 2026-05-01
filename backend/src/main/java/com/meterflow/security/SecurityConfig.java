package com.meterflow.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final ApiKeyFilter apiKeyFilter;

    public SecurityConfig(ApiKeyFilter apiKeyFilter) {
        this.apiKeyFilter = apiKeyFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                // ===============================
                //  Disable CSRF (REST)
                // ===============================
                .csrf(csrf -> csrf.disable())

                // ===============================
                //  CORS
                // ===============================
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration config = new CorsConfiguration();
                    config.setAllowedOrigins(List.of("*"));
                    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                    config.setAllowedHeaders(List.of("*"));
                    return config;
                }))

                // ===============================
                // Stateless
                // ===============================
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // ===============================
                //  AUTH RULES
                // ===============================
                .authorizeHttpRequests(auth -> auth

                        //  PUBLIC
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/plans/**").permitAll()
                        .requestMatchers("/payment/**").permitAll()

                        // USER APIs
                        .requestMatchers("/api/**").permitAll()
                        .requestMatchers("/usage/**").permitAll()
                        .requestMatchers("/billing/**").permitAll()
                        .requestMatchers("/apikey/**").permitAll()

                        //  IMPORTANT FIX (ADMIN ACCESS)
                        //  REMOVE hasRole → allow frontend-based role
                        .requestMatchers("/admin/**").permitAll()

                        //  CORS
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        //  fallback
                        .anyRequest().authenticated()
                )

                // ===============================
                //  API KEY FILTER
                // ===============================
                .addFilterBefore(apiKeyFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}