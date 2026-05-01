package com.meterflow.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.meterflow.model.ApiKey;

import java.util.Optional;
import java.util.List;

@Repository
public interface ApiKeyRepository extends JpaRepository<ApiKey, Long> {

    // ===============================
    //  BASIC OPERATIONS
    // ===============================

    Optional<ApiKey> findByKey(String key);

    boolean existsByKey(String key);


    // ===============================
    //  ACTIVE-BASED QUERIES
    // ===============================

    List<ApiKey> findByActiveTrue();

    List<ApiKey> findByActiveFalse();

    long countByActiveTrue();

    long countByActiveFalse();


    // ===============================
    //  USER-BASED QUERIES
    // ===============================

    List<ApiKey> findByUserId(Long userId);

    List<ApiKey> findByUserIdAndActiveTrue(Long userId);

    List<ApiKey> findByUserIdAndActiveFalse(Long userId);

    long countByUserId(Long userId);


    // ===============================
    //  SEARCH & FILTER
    // ===============================

    List<ApiKey> findByKeyContainingIgnoreCase(String keyword);

    List<ApiKey> findByUserIdAndKeyContainingIgnoreCase(Long userId, String keyword);

    //  NEW: search + active
    List<ApiKey> findByUserIdAndActiveTrueAndKeyContainingIgnoreCase(Long userId, String keyword);

    List<ApiKey> findByUserIdAndActiveFalseAndKeyContainingIgnoreCase(Long userId, String keyword);


    // ===============================
    //  PAGINATION
    // ===============================

    Page<ApiKey> findByUserId(Long userId, Pageable pageable);

    Page<ApiKey> findByUserIdAndKeyContainingIgnoreCase(
            Long userId,
            String keyword,
            Pageable pageable
    );

    Page<ApiKey> findByActiveTrue(Pageable pageable);

    Page<ApiKey> findByActiveFalse(Pageable pageable);

    //  NEW: pagination with filters
    Page<ApiKey> findByUserIdAndActiveTrue(Long userId, Pageable pageable);

    Page<ApiKey> findByUserIdAndActiveFalse(Long userId, Pageable pageable);

    Page<ApiKey> findByUserIdAndActiveTrueAndKeyContainingIgnoreCase(
            Long userId,
            String keyword,
            Pageable pageable
    );

    Page<ApiKey> findByUserIdAndActiveFalseAndKeyContainingIgnoreCase(
            Long userId,
            String keyword,
            Pageable pageable
    );


    // ===============================
    //  ADMIN HELPERS
    // ===============================

    // Count all keys
    long count();

    // Count keys per user (active only)
    long countByUserIdAndActiveTrue(Long userId);

    // Count keys per user (inactive only)
    long countByUserIdAndActiveFalse(Long userId);
}