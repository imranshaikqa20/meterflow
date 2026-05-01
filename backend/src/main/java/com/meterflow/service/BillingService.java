package com.meterflow.service;

import org.springframework.stereotype.Service;

import com.meterflow.repository.BillingRepository;
import com.meterflow.repository.UsageRepository;
import com.meterflow.repository.ApiKeyRepository;
import com.meterflow.model.Billing;
import com.meterflow.model.ApiKey;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Comparator;

@Service
public class BillingService {

    private final BillingRepository billingRepo;
    private final UsageRepository usageRepo;
    private final ApiKeyRepository apiKeyRepo;

    public BillingService(
            BillingRepository billingRepo,
            UsageRepository usageRepo,
            ApiKeyRepository apiKeyRepo
    ) {
        this.billingRepo = billingRepo;
        this.usageRepo = usageRepo;
        this.apiKeyRepo = apiKeyRepo;
    }

    // ===============================
    // 💰 1. GENERATE BILL
    // ===============================
    public Billing generateBill(String apiKey) {

        // ✅ 1. Validate input
        if (apiKey == null || apiKey.isEmpty()) {
            throw new RuntimeException("API Key is missing");
        }

        // ✅ 2. Validate API Key exists
        ApiKey key = apiKeyRepo.findByKey(apiKey)
                .orElseThrow(() -> new RuntimeException("Invalid API Key"));

        // ✅ 3. Get usage count
        long totalRequests = usageRepo.countByApiKey(apiKey);

        System.out.println("🔥 Generating bill for: " + apiKey);
        System.out.println("🔥 Total requests: " + totalRequests);

        // ✅ 4. Pricing logic
        double pricePerRequest = 0.5;
        double amount = totalRequests * pricePerRequest;

        // ✅ 5. Get latest bill
        List<Billing> bills = billingRepo.findByApiKey(apiKey);

        Billing latestBill = null;

        if (bills != null && !bills.isEmpty()) {
            latestBill = bills.stream()
                    .max(Comparator.comparing(Billing::getCreatedAt))
                    .orElse(null);
        }

        // 🔥 Avoid duplicate bills
        if (latestBill != null && latestBill.getTotalRequests() == totalRequests) {
            System.out.println("⚠ Same usage → returning existing bill");
            return latestBill;
        }

        // ===============================
        // ✅ 6. CREATE NEW BILL
        // ===============================
        Billing bill = new Billing();
        bill.setApiKey(apiKey);

        // 🔥 Optional: attach user
        if (key.getUserId() != null) {
            bill.setUserId(key.getUserId());
        }

        bill.setTotalRequests((int) totalRequests);
        bill.setAmount(amount);

        // ❌ REMOVED (IMPORTANT)
        // bill.setStatus("GENERATED");

        bill.setCreatedAt(LocalDateTime.now());

        Billing savedBill = billingRepo.save(bill);

        System.out.println("✅ Bill generated successfully: " + savedBill.getId());

        return savedBill;
    }

    // ===============================
    // 📊 2. GET ALL BILLS
    // ===============================
    public List<Billing> getBills(String apiKey) {

        if (apiKey == null || apiKey.isEmpty()) {
            throw new RuntimeException("API Key is required");
        }

        return billingRepo.findByApiKey(apiKey);
    }

    // ===============================
    // 🔥 3. GET LATEST BILL
    // ===============================
    public Billing getLatestBill(String apiKey) {

        List<Billing> bills = billingRepo.findByApiKey(apiKey);

        if (bills == null || bills.isEmpty()) {
            return null;
        }

        return bills.stream()
                .max(Comparator.comparing(Billing::getCreatedAt))
                .orElse(null);
    }
}