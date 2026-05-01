package com.meterflow.service;

import org.springframework.stereotype.Service;

import com.meterflow.model.User;
import com.meterflow.model.Role;
import com.meterflow.repository.UserRepository;

@Service
public class AuthService {

    private final UserRepository repo;

    public AuthService(UserRepository repo) {
        this.repo = repo;
    }

    // ===============================
    // ✅ 1. REGISTER USER (FIXED)
    // ===============================
    public User register(User user) {

        // 🔴 Validation
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new RuntimeException("Email is required");
        }

        if (user.getPassword() == null || user.getPassword().isBlank()) {
            throw new RuntimeException("Password is required");
        }

        // 🔴 Check if user already exists
        User existing = repo.findByEmail(user.getEmail());
        if (existing != null) {
            throw new RuntimeException("User already exists");
        }

        // ===============================
        // 🔥 ROLE LOGIC (IMPORTANT FIX)
        // ===============================

        // 👉 Allow ADMIN only for specific emails
        if ("admin@gmail.com".equalsIgnoreCase(user.getEmail())) {
            user.setRole(Role.ADMIN);
        } else {
            user.setRole(Role.USER);
        }

        // ===============================
        // 🔥 DEFAULT PLAN
        // ===============================
        if (user.getPlan() == null) {
            user.setPlan("FREE");
        }

        return repo.save(user);
    }

    // ===============================
    // ✅ 2. LOGIN USER
    // ===============================
    public User login(User user) {

        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new RuntimeException("Email is required");
        }

        if (user.getPassword() == null || user.getPassword().isBlank()) {
            throw new RuntimeException("Password is required");
        }

        User existing = repo.findByEmail(user.getEmail());

        if (existing == null) {
            throw new RuntimeException("User not found");
        }

        if (!existing.getPassword().equals(user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        return existing;
    }
}