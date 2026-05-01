package com.meterflow.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.meterflow.model.User;
import com.meterflow.model.ApiKey;
import com.meterflow.repository.UserRepository;
import com.meterflow.repository.BillingRepository;
import com.meterflow.repository.UsageRepository;
import com.meterflow.repository.ApiKeyRepository;

import java.util.*;

@RestController
@RequestMapping("/admin")
@CrossOrigin
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BillingRepository billingRepository;

    @Autowired
    private UsageRepository usageRepository;

    @Autowired
    private ApiKeyRepository apiKeyRepository;

    // ===============================
    // 👥 GET ALL USERS
    // ===============================
    @GetMapping("/users")
    public List<Map<String, Object>> getUsers() {

        List<User> users = userRepository.findAll();
        List<Map<String, Object>> result = new ArrayList<>();

        for (User u : users) {
            Map<String, Object> userData = new HashMap<>();
            userData.put("id", u.getId());
            userData.put("email", u.getEmail());
            userData.put("role", u.getRole());
            userData.put("plan", u.getPlan());
            userData.put("createdAt", u.getCreatedAt());

            result.add(userData);
        }

        return result;
    }

    // ===============================
    // TOTAL REVENUE
    // ===============================
    @GetMapping("/revenue")
    public Map<String, Object> getRevenue() {

        Double revenue = billingRepository.getTotalRevenue();

        Map<String, Object> response = new HashMap<>();
        response.put("totalRevenue", revenue != null ? revenue : 0);

        return response;
    }

    // ===============================
    // TRAFFIC ANALYTICS
    // ===============================
    @GetMapping("/traffic")
    public Map<String, Object> getTraffic() {

        Map<String, Object> data = new HashMap<>();

        data.put("totalRequests", usageRepository.totalRequests());
        data.put("perEndpoint", usageRepository.requestsPerEndpoint());
        data.put("daily", usageRepository.dailyTraffic());
        data.put("statusSummary", usageRepository.statusSummary());

        return data;
    }

    // ===============================
    //  GET ALL API KEYS
    // ===============================
    @GetMapping("/apikeys")
    public List<ApiKey> getAllApiKeys() {
        return apiKeyRepository.findAll();
    }

    // ===============================
    //  DISABLE API KEY
    // ===============================
    @PutMapping("/apikey/{id}/disable")
    public Map<String, String> disableApiKey(@PathVariable Long id) {

        ApiKey key = apiKeyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("API Key not found"));

        key.setActive(false);
        apiKeyRepository.save(key);

        return Map.of("message", "API Key Disabled Successfully");
    }

    // ===============================
    //  ENABLE API KEY
    // ===============================
    @PutMapping("/apikey/{id}/enable")
    public Map<String, String> enableApiKey(@PathVariable Long id) {

        ApiKey key = apiKeyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("API Key not found"));

        key.setActive(true);
        apiKeyRepository.save(key);

        return Map.of("message", "API Key Enabled Successfully");
    }
}