package com.meterflow.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.meterflow.repository.ApiKeyRepository;
import com.meterflow.model.ApiKey;
import com.meterflow.service.UsageService;

@Component
public class ApiKeyFilter extends OncePerRequestFilter {

    private static final String LOGGED_ATTR = "USAGE_LOGGED";

    //  RATE LIMIT CONFIG
    private static final int LIMIT = 100;
    private static final int WINDOW_SECONDS = 60;

    private final ApiKeyRepository repo;
    private final UsageService usageService;
    private final StringRedisTemplate redisTemplate;

    public ApiKeyFilter(ApiKeyRepository repo,
                        UsageService usageService,
                        StringRedisTemplate redisTemplate) {
        this.repo = repo;
        this.usageService = usageService;
        this.redisTemplate = redisTemplate;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain)
            throws ServletException, IOException {

        String path = req.getRequestURI();

        System.out.println("👉 Incoming request: " + path);

        //  1. Skip public endpoints
        if (isPublicEndpoint(path)) {
            chain.doFilter(req, res);
            return;
        }

        //  IMPORTANT: Only protect /api/**
        if (!path.startsWith("/api")) {
            chain.doFilter(req, res);
            return;
        }

        //  2. Get API Key
        String key = req.getHeader("x-api-key");

        if (key == null || key.isBlank()) {
            sendError(res, HttpServletResponse.SC_UNAUTHORIZED, "Missing API Key");
            return;
        }

        //  3. Validate API Key
        Optional<ApiKey> optionalKey = repo.findByKey(key);

        if (optionalKey.isEmpty()) {
            sendError(res, HttpServletResponse.SC_UNAUTHORIZED, "Invalid API Key");
            return;
        }

        ApiKey apiKey = optionalKey.get();

        //  FIXED (boolean check)
        if (!apiKey.isActive()) {
            sendError(res, HttpServletResponse.SC_FORBIDDEN, "API Key is disabled");
            return;
        }

        //  4. RATE LIMIT CHECK
        if (isRateLimited(key, res)) {
            return;
        }

        try {
            chain.doFilter(req, res);

            //  5. Log usage once
            if (shouldTrack(path) && req.getAttribute(LOGGED_ATTR) == null) {

                req.setAttribute(LOGGED_ATTR, true);

                String cleanPath = normalizeEndpoint(path);

                usageService.log(key, cleanPath, res.getStatus());

                System.out.println("✅ Logged: " + cleanPath);
            }

        } catch (Exception e) {

            if (shouldTrack(path) && req.getAttribute(LOGGED_ATTR) == null) {

                req.setAttribute(LOGGED_ATTR, true);

                String cleanPath = normalizeEndpoint(path);

                usageService.log(key, cleanPath, 500);
            }

            throw e;
        }
    }

    //  RATE LIMIT
    private boolean isRateLimited(String apiKey, HttpServletResponse res) throws IOException {

        try {
            String redisKey = "rate_limit:" + apiKey;

            String current = redisTemplate.opsForValue().get(redisKey);

            if (current == null) {
                redisTemplate.opsForValue().set(redisKey, "1", WINDOW_SECONDS, TimeUnit.SECONDS);
                res.setHeader("X-Rate-Limit-Remaining", String.valueOf(LIMIT - 1));
                return false;
            }

            int count;

            try {
                count = Integer.parseInt(current);
            } catch (NumberFormatException e) {
                count = 0;
            }

            if (count >= LIMIT) {
                sendError(res, 429, "Rate limit exceeded (" + LIMIT + " req/min)");
                return true;
            }

            Long newCount = redisTemplate.opsForValue().increment(redisKey);

            if (newCount != null) {
                res.setHeader("X-Rate-Limit-Remaining", String.valueOf(LIMIT - newCount));
            }

            return false;

        } catch (Exception e) {
            System.out.println("⚠️ Redis not available → skipping rate limit");
            return false;
        }
    }

    //  Normalize endpoint
    private String normalizeEndpoint(String path) {
        if (path == null) return "";

        return path
                .split("\\?")[0]
                .replaceAll("/key_[^/]+", "/key")
                .replaceAll("/\\d+", "/id");
    }

    //  Send error response
    private void sendError(HttpServletResponse res, int status, String message) throws IOException {
        res.setStatus(status);
        res.setContentType("application/json");
        res.getWriter().write("{\"error\":\"" + message + "\"}");
    }

    // Public endpoints
    private boolean isPublicEndpoint(String path) {
        return path.startsWith("/auth") ||
                path.startsWith("/apikey") ||
                path.startsWith("/usage") ||
                path.startsWith("/billing") ||
                path.startsWith("/plans") ||
                path.startsWith("/admin") || // 🔥 IMPORTANT (admin uses JWT, not API key)
                path.startsWith("/error") ||
                path.equals("/");
    }

    // Track only business APIs
    private boolean shouldTrack(String path) {
        return path.startsWith("/api");
    }
}