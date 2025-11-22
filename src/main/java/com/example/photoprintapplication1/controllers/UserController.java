package com.example.photoprintapplication.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @GetMapping("/profile")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public String getUserProfile() {
        return "User profile data";
    }

    @PutMapping("/profile")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public String updateUserProfile() {
        return "Profile updated";
    }
}
