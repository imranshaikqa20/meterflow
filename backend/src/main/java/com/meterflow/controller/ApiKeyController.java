package com.meterflow.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.meterflow.service.ApiKeyService;
import com.meterflow.model.ApiKey;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/apikey")
@CrossOrigin(origins = "*")
public class ApiKeyController {

    private final ApiKeyService service;

    public ApiKeyController(ApiKeyService service) {
        this.service = service;
    }

    // ===============================
    // GENERATE API KEY (USER)
    // ===============================
    @PostMapping("/generate/{userId}")
    public ResponseEntity<?> generateKey(@PathVariable Long userId) {
        try {
            System.out.println("Generating API Key for user: " + userId);

            ApiKey key = service.generateKey(userId);
            return ResponseEntity.ok(key);

        } catch (Exception e) {
            e.printStackTrace();

            return ResponseEntity.badRequest().body(error(e.getMessage()));
        }
    }

    // ===============================
    //  GET KEYS BY USER
    // ===============================
    @GetMapping("/all/{userId}")
    public ResponseEntity<?> getAllKeysByUser(@PathVariable Long userId) {
        try {
            System.out.println("HIT /apikey/all/" + userId);

            List<ApiKey> keys = service.getKeysByUser(userId);
            return ResponseEntity.ok(keys);

        } catch (Exception e) {
            e.printStackTrace();

            return ResponseEntity.badRequest().body(error("Failed to fetch API keys"));
        }
    }

    // ===============================
    // GET ACTIVE KEYS
    // ===============================
    @GetMapping("/active/{userId}")
    public ResponseEntity<?> getActiveKeys(@PathVariable Long userId) {
        try {
            List<ApiKey> keys = service.getActiveKeysByUser(userId);
            return ResponseEntity.ok(keys);

        } catch (Exception e) {
            e.printStackTrace();

            return ResponseEntity.badRequest().body(error("Failed to fetch active keys"));
        }
    }

    // ===============================
    // USER REVOKE
    // ===============================
    @DeleteMapping("/revoke/{id}")
    public ResponseEntity<?> revokeKey(@PathVariable Long id) {
        try {
            service.revokeKey(id);

            return ResponseEntity.ok(success("API Key revoked successfully"));

        } catch (Exception e) {
            e.printStackTrace();

            return ResponseEntity.badRequest().body(error("Failed to revoke API key"));
        }
    }

    // ===============================
    // ADMIN: GET ALL KEYS
    // ===============================
    @GetMapping("/all")
    public ResponseEntity<?> getAllKeys() {
        try {
            List<ApiKey> keys = service.getAllKeys();
            return ResponseEntity.ok(keys);

        } catch (Exception e) {
            e.printStackTrace();

            return ResponseEntity.badRequest().body(error("Failed to fetch all keys"));
        }
    }

    // ===============================
    // ADMIN: DISABLE KEY
    // ===============================
    @PutMapping("/admin/apikey/{id}/disable")
    public ResponseEntity<?> disableApiKey(@PathVariable Long id) {
        try {
            service.revokeKey(id); // 🔥 uses same logic

            return ResponseEntity.ok(success("API Key disabled successfully"));

        } catch (Exception e) {
            e.printStackTrace();

            return ResponseEntity.badRequest().body(error("Failed to disable API key"));
        }
    }

    // ===============================
    //  RESPONSE HELPERS
    // ===============================
    private Map<String, String> error(String msg) {
        Map<String, String> map = new HashMap<>();
        map.put("error", msg);
        return map;
    }

    private Map<String, String> success(String msg) {
        Map<String, String> map = new HashMap<>();
        map.put("message", msg);
        return map;
    }
}