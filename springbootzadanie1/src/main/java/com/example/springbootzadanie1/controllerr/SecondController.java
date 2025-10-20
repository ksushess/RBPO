package com.example.springbootzadanie1.controllerr;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SecondController {
    @PostMapping("/second")
    public String second() {
        return "info show again";
    }
}
