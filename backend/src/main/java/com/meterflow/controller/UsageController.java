package com.meterflow.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.meterflow.service.UsageService;
import com.meterflow.service.UsageService.UsageResponse;
import com.meterflow.model.UsageLog;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/usage")
@CrossOrigin(origins = "*")
public class UsageController {

    private final UsageService service;

    public UsageController(UsageService service) {
        this.service = service;
    }

    //  Log API usage
    @PostMapping("/log")
    public ResponseEntity<?> log() {
        try {
            service.log("test-key", "/api/test", 200);

            Map<String, String> res = new HashMap<>();
            res.put("message", "Usage logged successfully");

            return ResponseEntity.ok(res);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(error(e));
        }
    }

    // ✅ 2. Get all usage logs
    @GetMapping("/all")
    public ResponseEntity<?> getAllUsage() {
        try {
            List<UsageLog> logs = service.getAllUsage();
            return ResponseEntity.ok(logs);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(error(e));
        }
    }

    //  Dashboard summary (FIXED)
    @GetMapping("/summary")
    public ResponseEntity<?> getUsageSummary() {
        try {
            // ✅ NOW USING DTO (NO Map)
            List<UsageResponse> summary = service.getUsageSummary();
            return ResponseEntity.ok(summary);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(error(e));
        }
    }

    //  Usage per API key
    @GetMapping("/by-key/{key}")
    public ResponseEntity<?> getUsageByKey(@PathVariable String key) {
        try {
            return ResponseEntity.ok(service.getUsageByKey(key));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(error(e));
        }
    }

    // Count usage per API key
    @GetMapping("/count/{key}")
    public ResponseEntity<?> countByKey(@PathVariable String key) {
        try {
            long count = service.countByKey(key);

            Map<String, Object> res = new HashMap<>();
            res.put("apiKey", key);
            res.put("count", count);

            return ResponseEntity.ok(res);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(error(e));
        }
    }

    @GetMapping("/recent")
    public ResponseEntity<?> recentLogs() {
        try {
            return ResponseEntity.ok(service.getRecentLogs());

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(error(e));
        }
    }


    private Map<String, String> error(Exception e) {
        Map<String, String> err = new HashMap<>();
        err.put("error", e.getMessage());
        return err;
    }
}