package com.meterflow.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.meterflow.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
}