package com.example.photoprintapplication.config;

import com.example.photoprintapplication.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                )
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/", "/api/home/**", "/api/auth/**", "/api/csrf-token").permitAll()
                        .requestMatchers("/api/orders/my-orders", "/api/orders/create").hasRole("USER")
                        .requestMatchers("/api/formats/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/api/photos/my-photos/**").hasRole("USER")
                        .requestMatchers("/api/business/orders/calculate", "/api/business/orders/calculate-price").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/api/business/statistics/popular-formats").hasAnyRole("ADMIN")
                        .requestMatchers("/api/admin/**", "/api/customers/**", "/api/business/**", "/api/deliveries/**").hasRole("ADMIN")
                        .requestMatchers("/api/orders/**").hasRole("ADMIN")
                        .requestMatchers("/api/photos/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .httpBasic(httpBasic -> {});

        return http.build();
    }
}