package com.meterflow.controller;

import com.razorpay.*;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.*;

import com.meterflow.model.User;
import com.meterflow.repository.UserRepository;

@RestController
@RequestMapping("/payment")
@CrossOrigin("*")
public class PaymentController {

    private final UserRepository userRepo;

    public PaymentController(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    private static final String KEY = "rzp_test";
    private static final String SECRET = "xxxxxxxx";

    //  Create Order
    @PostMapping("/create-order")
    public ResponseEntity<?> createOrder(@RequestParam int amount) {
        try {
            RazorpayClient client = new RazorpayClient(KEY, SECRET);

            JSONObject options = new JSONObject();
            options.put("amount", amount * 100); // paise
            options.put("currency", "INR");

            Order order = client.orders.create(options);

            return ResponseEntity.ok(order.toString());

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Order creation failed");
        }
    }

    // Verify Payment + Upgrade Plan
    @PostMapping("/verify")
    public ResponseEntity<?> verifyPayment(@RequestBody Map<String, String> data) {
        try {
            String razorpayOrderId = data.get("razorpay_order_id");
            String razorpayPaymentId = data.get("razorpay_payment_id");
            String razorpaySignature = data.get("razorpay_signature");
            Long userId = Long.parseLong(data.get("userId"));

            // 🔐 VERIFY SIGNATURE
            String payload = razorpayOrderId + "|" + razorpayPaymentId;

            String generatedSignature = hmacSHA256(payload, SECRET);

            if (!generatedSignature.equals(razorpaySignature)) {
                return ResponseEntity.status(400).body("Invalid signature");
            }


            User user = userRepo.findById(userId).orElseThrow();

            user.setPlan("PRO");
            user.setPlanExpiry(java.time.LocalDateTime.now().plusDays(30));

            userRepo.save(user);

            return ResponseEntity.ok("Payment verified & plan upgraded");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Verification failed");
        }
    }


    private String hmacSHA256(String data, String secret) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret.getBytes(), "HmacSHA256"));
        byte[] hash = mac.doFinal(data.getBytes());
        return bytesToHex(hash);
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder hex = new StringBuilder(2 * bytes.length);
        for (byte b : bytes) {
            hex.append(String.format("%02x", b));
        }
        return hex.toString();
    }
}