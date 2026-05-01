package com.meterflow.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "plans")
public class Plan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //  Plan name (FREE, PRO, ENTERPRISE)
    @Column(nullable = false, unique = true)
    private String name;

    //  Monthly price
    @Column(nullable = false)
    private double price;

    //  Rate limit per minute
    @Column(name = "request_limit")
    private int requestLimit;

    //  Subscription duration (in days)
    @Column(name = "duration_days")
    private int durationDays;

    //  Plan description
    private String description;

    // ⏱ Created time
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // ===============================
    //  AUTO TIMESTAMP
    // ===============================
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    // ===============================
    // GETTERS
    // ===============================

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getRequestLimit() {
        return requestLimit;
    }

    public int getDurationDays() {
        return durationDays;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // ===============================
    // SETTERS
    // ===============================

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setRequestLimit(int requestLimit) {
        this.requestLimit = requestLimit;
    }

    public void setDurationDays(int durationDays) {
        this.durationDays = durationDays;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}