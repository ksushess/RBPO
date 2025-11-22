package com.example.photoprintapplication.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
public class SimpleCsrfFilter implements Filter {

    private final String secretToken;

    private static final List<String> SAFE_METHODS = Arrays.asList("GET", "HEAD", "OPTIONS", "TRACE");
    private static final List<String> PUBLIC_ENDPOINTS = Arrays.asList(
            "/api/auth/register", "/", "/api/home", "/api/csrf-token"
    );

    public SimpleCsrfFilter() {
        this.secretToken = "token" + System.currentTimeMillis();
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        System.out.println("Token: " + this.secretToken);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String method = httpRequest.getMethod();
        String path = httpRequest.getRequestURI();

        if (SAFE_METHODS.contains(method) || isPublicEndpoint(path)) {
            chain.doFilter(request, response);
            return;
        }

        String clientToken = httpRequest.getHeader("X-CSRF-TOKEN");

        if (secretToken.equals(clientToken)) {
            chain.doFilter(request, response);
        } else {
            httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
            httpResponse.setContentType("application/json");
            httpResponse.getWriter().write(
                    "{\"error\": \"Invalid CSRF token\", \"message\": \"Include X-CSRF-TOKEN header\"}"
            );
        }
    }

    private boolean isPublicEndpoint(String path) {
        return PUBLIC_ENDPOINTS.stream().anyMatch(path::startsWith);
    }

    public String getSecretToken() {
        return secretToken;
    }
}