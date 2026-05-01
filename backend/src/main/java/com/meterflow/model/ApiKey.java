package com.meterflow.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "api_keys")
public class ApiKey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 🔑 MATCH DB COLUMN ✅
    @Column(name = "api_key", unique = true, nullable = false, length = 100)
    private String key;

    @Column(nullable = false)
    private boolean active = true;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    public Long getId() { return id; }
    public String getKey() { return key; }
    public boolean isActive() { return active; }
    public Long getUserId() { return userId; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setId(Long id) { this.id = id; }
    public void setKey(String key) { this.key = key; }
    public void setActive(boolean active) { this.active = active; }
    public void setUserId(Long userId) { this.userId = userId; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public void disable() { this.active = false; }
    public void enable() { this.active = true; }
    public boolean isDisabled() { return !this.active; }
}