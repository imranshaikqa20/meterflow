package com.meterflow.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.meterflow.model.Billing;
import com.meterflow.repository.BillingRepository;
import com.meterflow.repository.UsageRepository;
import com.meterflow.repository.ApiKeyRepository;
import com.meterflow.model.ApiKey;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/billing")
@CrossOrigin(origins = "*")
public class BillingController {

    private final UsageRepository usageRepo;
    private final BillingRepository billingRepo;
    private final ApiKeyRepository apiKeyRepo;

    public BillingController(UsageRepository usageRepo,
                             BillingRepository billingRepo,
                             ApiKeyRepository apiKeyRepo) {
        this.usageRepo = usageRepo;
        this.billingRepo = billingRepo;
        this.apiKeyRepo = apiKeyRepo;
    }

    // ===============================
    //  GET BILLING (AUTO GENERATE)
    // ===============================
    @GetMapping("/{key}")
    public ResponseEntity<?> getBilling(@PathVariable String key) {

        System.out.println("🔥 GET BILLING: " + key);

        try {
            List<Billing> bills = billingRepo.findByApiKey(key);

            ApiKey apiKey = apiKeyRepo.findByKey(key)
                    .orElseThrow(() -> new RuntimeException("Invalid API Key"));

            // ✅ Auto-generate if no bills
            if (bills == null || bills.isEmpty()) {

                System.out.println("⚠️ No bills → generating new one");

                long usageCount = usageRepo.countByApiKey(key);
                double amount = usageCount * 0.5;

                Billing bill = new Billing();
                bill.setApiKey(key);
                bill.setUserId(apiKey.getUserId());
                bill.setTotalRequests((int) usageCount);
                bill.setAmount(amount);
                bill.setCreatedAt(LocalDateTime.now());

                billingRepo.save(bill);

                // fetch again
                bills = billingRepo.findByApiKey(key);
            }

            return ResponseEntity.ok(bills);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("ERROR FETCHING BILLING");
        }
    }

    // ===============================
    //  GENERATE BILL
    // ===============================
    @PostMapping("/generate/{key}")
    public ResponseEntity<?> generateBill(@PathVariable String key) {

        System.out.println("🔥 GENERATE BILL: " + key);

        try {

            ApiKey apiKey = apiKeyRepo.findByKey(key)
                    .orElseThrow(() -> new RuntimeException("Invalid API Key"));

            long usageCount = usageRepo.countByApiKey(key);
            double amount = usageCount * 0.5;

            Billing bill = new Billing();
            bill.setApiKey(key);
            bill.setUserId(apiKey.getUserId());
            bill.setTotalRequests((int) usageCount);
            bill.setAmount(amount);
            bill.setCreatedAt(LocalDateTime.now());

            Billing saved = billingRepo.save(bill);

            return ResponseEntity.ok(saved);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("ERROR GENERATING BILL");
        }
    }
}