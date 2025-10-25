package com.example.photoprintapplication.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "API фотопечати доступно по:  /api/customers, /api/orders, /api/formats, /api/photos, /api/deliveries";
    }
}