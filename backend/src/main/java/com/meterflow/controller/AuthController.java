package com.meterflow.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.meterflow.model.User;
import com.meterflow.service.AuthService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin
public class AuthController {

    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    // ===============================
    //  REGISTER USER (FIXED)
    // ===============================
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        try {

            System.out.println("Register request: " + user.getEmail());

            User savedUser = service.register(user);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "User registered successfully");
            response.put("userId", savedUser.getId());
            response.put("email", savedUser.getEmail());
            response.put("role", savedUser.getRole().name());
            response.put("plan", savedUser.getPlan());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();

            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());

            return ResponseEntity.badRequest().body(error);
        }
    }

    // ===============================
    // LOGIN USER
    // ===============================
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        try {

            System.out.println("Login request: " + user.getEmail());

            User loggedUser = service.login(user);
            String token = "dummy-token";
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("userId", loggedUser.getId());
            response.put("email", loggedUser.getEmail());
            response.put("role", loggedUser.getRole().name());
            response.put("plan", loggedUser.getActivePlan());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();

            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());

            return ResponseEntity.status(401).body(error);
        }
    }
}