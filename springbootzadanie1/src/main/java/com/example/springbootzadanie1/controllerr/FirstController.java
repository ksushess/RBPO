package com.example.springbootzadanie1.controllerr;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FirstController {
    @GetMapping("/info")
    public String first() {
        return "show info";
    }
}
