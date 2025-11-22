package com.example.photoprintapplication.config;

import com.example.photoprintapplication.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final SimpleCsrfFilter simpleCsrfFilter;

    public SecurityConfig(CustomUserDetailsService userDetailsService, SimpleCsrfFilter simpleCsrfFilter) {
        this.userDetailsService = userDetailsService;
        this.simpleCsrfFilter = simpleCsrfFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .userDetailsService(userDetailsService)

                //            .csrf(csrf -> csrf.disable())
                .addFilterBefore(simpleCsrfFilter, BasicAuthenticationFilter.class)

                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/", "/api/home/**", "/api/auth/register").permitAll()

                        .requestMatchers("/api/orders/my-orders", "/api/orders/create").hasRole("USER")
                        .requestMatchers("/api/formats/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/api/photos/my-photos/**").hasRole("USER")
                        .requestMatchers("/api/business/orders/calculate, /api/business/orders/calculate-price").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/api/business/statistics/popular-formats").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/api/admin/**", "/api/customers/**",
                                "/api/business/**", "/api/deliveries/**").hasRole("ADMIN")
                        .requestMatchers("/api/orders/**").hasRole("ADMIN")
                        .requestMatchers("/api/photos/**","/api/csrf-token").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .httpBasic(httpBasic -> {});

        return http.build();
    }
}