package com.meterflow.controller;

import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api")
@CrossOrigin("*")
public class TestController {

    // =========================================
    //  HELLO API
    // =========================================
    @GetMapping("/hello")
    public String hello() {
        return "Hello API - Working! 🚀";
    }

    // =========================================
    //  WEATHER API (DUMMY DATA)
    // =========================================
    @GetMapping("/weather")
    public Map<String, Object> weather() {

        Map<String, Object> data = new HashMap<>();
        data.put("city", "Hyderabad");
        data.put("temperature", "32°C");
        data.put("status", "Sunny");

        return data;
    }

    // =========================================
    //  PAYMENT API (DUMMY)
    // =========================================
    @GetMapping("/payment")
    public Map<String, Object> payment() {

        Map<String, Object> data = new HashMap<>();
        data.put("status", "SUCCESS");
        data.put("amount", 499);
        data.put("currency", "INR");

        return data;
    }

    // =========================================
    //  USER API (DUMMY)
    // =========================================
    @GetMapping("/user")
    public Map<String, Object> user() {

        Map<String, Object> data = new HashMap<>();
        data.put("id", 1);
        data.put("name", "Test User");
        data.put("role", "DEVELOPER");

        return data;
    }

    // =========================================
    // RANDOM DATA API (FOR TESTING)
    // =========================================
    @GetMapping("/random")
    public Map<String, Object> random() {

        Random rand = new Random();

        Map<String, Object> data = new HashMap<>();
        data.put("value", rand.nextInt(100));
        data.put("timestamp", new Date());

        return data;
    }
}