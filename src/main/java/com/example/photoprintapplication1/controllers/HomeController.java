package com.example.photoprintapplication1.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "API: /api/customers, /api/orders, /api/formats, /api/photos, /api/deliveries";
    }
}
