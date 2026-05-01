package com.meterflow.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "billing")
public class Billing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "api_key", nullable = false)
    private String apiKey;

    @Column(name = "user_id")
    private Long userId;


    @Column(name = "total_requests")
    private Integer totalRequests;

    @Column(nullable = false)
    private double amount;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // ===============================
    // AUTO DEFAULTS
    // ===============================
    @PrePersist
    public void prePersist() {

        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }

        //  Prevent null crash
        if (this.totalRequests == null) {
            this.totalRequests = 0;
        }
    }

    // ===============================
    // GETTERS
    // ===============================

    public Long getId() {
        return id;
    }

    public String getApiKey() {
        return apiKey;
    }

    public Long getUserId() {
        return userId;
    }

    public Integer getTotalRequests() {
        return totalRequests;
    }

    public double getAmount() {
        return amount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // Optional helpers
    public int getUsageCount() {
        return totalRequests != null ? totalRequests : 0;
    }

    public LocalDateTime getGeneratedAt() {
        return createdAt;
    }

    // ===============================
    // SETTERS
    // ===============================

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setTotalRequests(Integer totalRequests) {
        this.totalRequests = totalRequests;
    }

    public void setUsageCount(int usageCount) {
        this.totalRequests = usageCount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setGeneratedAt(LocalDateTime generatedAt) {
        this.createdAt = generatedAt;
    }
}