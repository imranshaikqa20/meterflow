package com.meterflow.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    //  ROLE ENUM (FIXED)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER;

    //  PLAN (FREE / PRO)
    @Column(nullable = false)
    private String plan = "FREE";

    //  Subscription expiry
    @Column(name = "plan_expiry")
    private LocalDateTime planExpiry;

    //  Created timestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // ===============================
    //  CONSTRUCTORS
    // ===============================

    public User() {}

    public User(String email, String password) {
        this.email = email;
        this.password = password;
        this.role = Role.USER; // 🔥 always default USER
        this.plan = "FREE";
    }

    // ===============================
    //  AUTO TIMESTAMP
    // ===============================
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();

        if (this.plan == null) {
            this.plan = "FREE";
        }

        if (this.role == null) {
            this.role = Role.USER;
        }
    }

    // ===============================
    // GETTERS
    // ===============================

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public Role getRole() {
        return role;
    }

    public String getPlan() {
        return plan;
    }

    public LocalDateTime getPlanExpiry() {
        return planExpiry;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // ===============================
    //  SETTERS
    // ===============================

    public void setId(Long id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void setPlan(String plan) {
        this.plan = plan;
    }

    public void setPlanExpiry(LocalDateTime planExpiry) {
        this.planExpiry = planExpiry;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // ===============================
    //  HELPER METHODS
    // ===============================

    //  Check if plan expired
    public boolean isPlanExpired() {
        if (planExpiry == null) return false;
        return LocalDateTime.now().isAfter(planExpiry);
    }

    //  Get active plan
    public String getActivePlan() {
        if (isPlanExpired()) {
            return "FREE";
        }
        return plan;
    }

    //  ROLE HELPERS (VERY USEFUL)
    public boolean isAdmin() {
        return this.role == Role.ADMIN;
    }

    public boolean isUser() {
        return this.role == Role.USER;
    }
}