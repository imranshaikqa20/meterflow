package com.meterflow.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "usage_logs", indexes = {
        @Index(name = "idx_api_key", columnList = "api_key"),
        @Index(name = "idx_endpoint", columnList = "endpoint"),
        @Index(name = "idx_timestamp", columnList = "timestamp")
})
public class UsageLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //  API Key used
    @Column(name = "api_key", nullable = false)
    private String apiKey;

    //  API endpoint
    @Column(nullable = false)
    private String endpoint;

    // ⏱ Timestamp of request
    @Column(nullable = false)
    private LocalDateTime timestamp;

    //  HTTP status (200, 401, etc.)
    @Column(nullable = false)
    private int status;

    //  Default constructor
    public UsageLog() {
    }

    //  Parameterized constructor
    public UsageLog(String apiKey, String endpoint, int status) {
        this.apiKey = apiKey;
        this.endpoint = endpoint;
        this.status = status;
    }

    //  Auto-set timestamp (SAFE)
    @PrePersist
    public void prePersist() {
        if (this.timestamp == null) {
            this.timestamp = LocalDateTime.now();
        }
    }

    //  Getters
    public Long getId() {
        return id;
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }

    //  Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public void setStatus(int status) {
        this.status = status;
    }


    @Override
    public String toString() {
        return "UsageLog{" +
                "id=" + id +
                ", apiKey='" + apiKey + '\'' +
                ", endpoint='" + endpoint + '\'' +
                ", timestamp=" + timestamp +
                ", status=" + status +
                '}';
    }
}