package com.meterflow.service;

import org.springframework.stereotype.Service;
import com.meterflow.model.ApiKey;
import com.meterflow.repository.ApiKeyRepository;

import java.util.List;
import java.util.UUID;

@Service
public class ApiKeyService {

    private final ApiKeyRepository repo;

    public ApiKeyService(ApiKeyRepository repo) {
        this.repo = repo;
    }

    // ===============================
    // 🔥 1. GENERATE API KEY (USER)
    // ===============================
    public ApiKey generateKey(Long userId) {

        if (userId == null) {
            throw new RuntimeException("User ID is required to generate API key");
        }

        ApiKey key = new ApiKey();
        key.setKey("key_" + UUID.randomUUID());

        // ✅ BOOLEAN ACTIVE FLAG
        key.setActive(true);

        key.setUserId(userId);

        return repo.save(key);
    }

    // ===============================
    // 🔥 2. GET KEYS BY USER
    // ===============================
    public List<ApiKey> getKeysByUser(Long userId) {

        if (userId == null) {
            throw new RuntimeException("User ID is required");
        }

        return repo.findByUserId(userId);
    }

    // ===============================
    // 🔥 3. GET ALL KEYS (ADMIN)
    // ===============================
    public List<ApiKey> getAllKeys() {
        return repo.findAll();
    }

    // ===============================
    // 🔥 4. REVOKE (DISABLE) KEY
    // ===============================
    public void revokeKey(Long id) {

        ApiKey key = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("API Key not found with id: " + id));

        // 🔥 Disable key
        key.setActive(false);

        repo.save(key);
    }

    // ===============================
    // 🔥 5. GET ACTIVE KEYS
    // ===============================
    public List<ApiKey> getActiveKeysByUser(Long userId) {

        if (userId == null) {
            throw new RuntimeException("User ID is required");
        }

        return repo.findByUserIdAndActiveTrue(userId);
    }

    // ===============================
    // 🔥 6. VALIDATE API KEY
    // ===============================
    public boolean isValidKey(String keyValue) {

        if (keyValue == null || keyValue.isEmpty()) {
            return false;
        }

        return repo.findByKey(keyValue)
                .map(ApiKey::isActive)
                .orElse(false);
    }

    // ===============================
    // 🔥 7. SEARCH KEYS
    // ===============================
    public List<ApiKey> searchKeys(Long userId, String keyword) {

        if (userId == null) {
            throw new RuntimeException("User ID is required");
        }

        if (keyword == null || keyword.isEmpty()) {
            return repo.findByUserId(userId);
        }

        return repo.findByUserIdAndKeyContainingIgnoreCase(userId, keyword);
    }

    // ===============================
    // 🔥 8. ENABLE KEY (OPTIONAL)
    // ===============================
    public void enableKey(Long id) {

        ApiKey key = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("API Key not found"));

        key.setActive(true);
        repo.save(key);
    }
}