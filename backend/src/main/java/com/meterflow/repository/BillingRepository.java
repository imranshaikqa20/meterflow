package com.meterflow.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.meterflow.model.Billing;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;

@Repository
public interface BillingRepository extends JpaRepository<Billing, Long> {

    // ===============================
    //  BASIC QUERIES
    // ===============================

    // Get all bills by API key
    List<Billing> findByApiKey(String apiKey);

    // Get all bills by user
    List<Billing> findByUserId(Long userId);

    // Count bills per user
    long countByUserId(Long userId);


    // ===============================
    //  DATE FILTER (IMPORTANT)
    // ===============================

    // Billing between dates
    List<Billing> findByUserIdAndCreatedAtBetween(
            Long userId,
            LocalDateTime start,
            LocalDateTime end
    );


    // ===============================
    //  ADMIN ANALYTICS
    // ===============================

    // Total revenue
    @Query("SELECT COALESCE(SUM(b.amount), 0) FROM Billing b")
    Double getTotalRevenue();

    // Revenue per user
    @Query("SELECT b.userId, SUM(b.amount) FROM Billing b GROUP BY b.userId")
    List<Object[]> revenuePerUser();

    // Revenue per API key
    @Query("SELECT b.apiKey, SUM(b.amount) FROM Billing b GROUP BY b.apiKey")
    List<Object[]> revenuePerApiKey();

    // Daily revenue
    @Query("SELECT DATE(b.createdAt), SUM(b.amount) FROM Billing b GROUP BY DATE(b.createdAt)")
    List<Object[]> dailyRevenue();
}