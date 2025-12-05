package com.example.photoprintapplication.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.web.csrf.CsrfToken;
import java.util.Map;

@RestController
public class CsrfTokenController {

    @GetMapping("/api/csrf-token")
    public Map<String, String> getCsrfToken(CsrfToken token) {
        return Map.of("token", token.getToken());
    }
}