package com.meterflow.service;

import org.springframework.stereotype.Service;

import com.meterflow.repository.UsageRepository;
import com.meterflow.model.UsageLog;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsageService {

    private final UsageRepository repo;

    public UsageService(UsageRepository repo) {
        this.repo = repo;
    }

    // ===============================
    // ✅ 1. Log API usage
    // ===============================
    public void log(String apiKey, String endpoint, int status) {

        UsageLog log = new UsageLog();

        log.setApiKey(apiKey);
        log.setEndpoint(normalizeEndpoint(endpoint)); // 🔥 safe
        log.setStatus(status);

        repo.save(log);
    }

    // ===============================
    // ✅ 2. Get all logs
    // ===============================
    public List<UsageLog> getAllUsage() {
        return repo.findAll();
    }

    // ===============================
    // 🔥 3. Usage summary (FIXED)
    // ===============================
    public List<UsageResponse> getUsageSummary() {

        // 🔥 FIX: use correct repository method
        List<Object[]> results = repo.requestsPerEndpoint();

        return results.stream()
                .map(obj -> new UsageResponse(
                        obj[0] != null ? obj[0].toString() : "Unknown API",
                        ((Number) obj[1]).intValue()
                ))
                .collect(Collectors.toList());
    }

    // ===============================
    // 🔥 4. Get usage by API key
    // ===============================
    public List<UsageLog> getUsageByKey(String key) {
        return repo.findByApiKey(key);
    }

    // ===============================
    // 🔥 5. Count usage per key
    // ===============================
    public long countByKey(String key) {
        return repo.countByApiKey(key);
    }

    // ===============================
    // 🔥 6. Recent logs
    // ===============================
    public List<UsageLog> getRecentLogs() {
        return repo.findTop10ByOrderByTimestampDesc();
    }

    // ===============================
    // 🔥 7. Usage grouped by API key
    // ===============================
    public List<UsageResponse> getUsageByApiKey() {

        List<Object[]> results = repo.requestsPerApiKey();

        return results.stream()
                .map(obj -> new UsageResponse(
                        obj[0] != null ? obj[0].toString() : "Unknown Key",
                        ((Number) obj[1]).intValue()
                ))
                .collect(Collectors.toList());
    }

    // ===============================
    // 🔥 Endpoint normalization
    // ===============================
    private String normalizeEndpoint(String endpoint) {

        if (endpoint == null || endpoint.isBlank()) {
            return "Unknown API";
        }

        return endpoint
                .split("\\?")[0]                  // remove query params
                .replaceAll("/key_[^/]+", "/key")
                .replaceAll("/\\d+", "/id");
    }

    // ===============================
    // ✅ DTO for charts
    // ===============================
    public static class UsageResponse {
        private String label;
        private int value;

        public UsageResponse(String label, int value) {
            this.label = label;
            this.value = value;
        }

        public String getLabel() {
            return label;
        }

        public int getValue() {
            return value;
        }
    }
}