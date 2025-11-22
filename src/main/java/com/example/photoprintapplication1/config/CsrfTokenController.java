package com.example.photoprintapplication.controllers;

import com.example.photoprintapplication.config.SimpleCsrfFilter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CsrfTokenController {

    private final SimpleCsrfFilter csrfFilter;

    public CsrfTokenController(SimpleCsrfFilter csrfFilter) {
        this.csrfFilter = csrfFilter;
    }

    @GetMapping("/api/csrf-token")
    public String getCsrfToken() {
        return csrfFilter.getSecretToken();
    }
}