package com.meterflow.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.meterflow.model.UsageLog;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;

@Repository
public interface UsageRepository extends JpaRepository<UsageLog, Long> {

    // ===============================
    //  BASIC QUERIES
    // ===============================
    List<UsageLog> findByApiKey(String apiKey);

    long countByApiKey(String apiKey);

    // ===============================
    // FILTERS
    // ===============================
    List<UsageLog> findByEndpoint(String endpoint);

    List<UsageLog> findByStatus(int status);

    List<UsageLog> findByApiKeyAndStatus(String apiKey, int status);

    // ===============================
    //  DATE FILTER (BILLING)
    // ===============================
    List<UsageLog> findByApiKeyAndTimestampBetween(
            String apiKey,
            LocalDateTime start,
            LocalDateTime end
    );

    // ===============================
    //  ADMIN ANALYTICS
    // ===============================

    // Total requests
    @Query("SELECT COUNT(u) FROM UsageLog u")
    Long totalRequests();

    // =========================================
    //  FIXED: Usage per endpoint (IMPORTANT)
    // =========================================
    @Query("""
        SELECT 
            COALESCE(u.endpoint, 'Unknown API'),
            COUNT(u)
        FROM UsageLog u
        GROUP BY u.endpoint
        ORDER BY COUNT(u) DESC
    """)
    List<Object[]> requestsPerEndpoint();

    // =========================================
    //  Usage per API key
    // =========================================
    @Query("""
        SELECT u.apiKey, COUNT(u)
        FROM UsageLog u
        GROUP BY u.apiKey
    """)
    List<Object[]> requestsPerApiKey();

    // =========================================
    //  Daily traffic
    // =========================================
    @Query("""
        SELECT FUNCTION('DATE', u.timestamp), COUNT(u)
        FROM UsageLog u
        GROUP BY FUNCTION('DATE', u.timestamp)
        ORDER BY FUNCTION('DATE', u.timestamp)
    """)
    List<Object[]> dailyTraffic();

    // =========================================
    // Status summary
    // =========================================
    @Query("""
        SELECT u.status, COUNT(u)
        FROM UsageLog u
        GROUP BY u.status
    """)
    List<Object[]> statusSummary();

    // ===============================
    // ⏱ RECENT LOGS
    // ===============================
    List<UsageLog> findTop10ByOrderByTimestampDesc();
}