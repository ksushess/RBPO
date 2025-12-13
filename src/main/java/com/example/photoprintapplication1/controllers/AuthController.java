package com.example.photoprintapplication1.controllers;

import com.example.photoprintapplication1.dto.AuthResponse;
import com.example.photoprintapplication1.dto.RegisterRequest;
import com.example.photoprintapplication1.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        try {
            String message = authService.register(request);
            return ResponseEntity.ok(new AuthResponse(
                    request.getUsername(),
                    "USER",
                    message
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new AuthResponse(null, null, "Registration failed: " + e.getMessage()));
        }
    }

    @GetMapping("/login")
    public ResponseEntity<AuthResponse> login() {
        return ResponseEntity.ok(new AuthResponse(
                null, null, "Use Basic Authentication for login"
        ));
    }
}
