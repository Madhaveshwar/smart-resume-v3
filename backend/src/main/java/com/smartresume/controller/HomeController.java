package com.smartresume.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*") // allow frontend (Vercel)
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "Smart Resume Backend is Running 🚀";
    }
}