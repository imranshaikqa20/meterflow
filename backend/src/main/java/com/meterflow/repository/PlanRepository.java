package com.meterflow.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.meterflow.model.Plan;
import java.util.Optional;

public interface PlanRepository extends JpaRepository<Plan, Long> {

    Optional<Plan> findByName(String name);
}