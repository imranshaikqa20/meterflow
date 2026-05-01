package com.meterflow.service;

import org.springframework.stereotype.Service;
import com.meterflow.model.Plan;
import com.meterflow.repository.PlanRepository;

import java.util.List;

@Service
public class PlanService {

    private final PlanRepository planRepo;

    public PlanService(PlanRepository planRepo) {
        this.planRepo = planRepo;
    }

    public List<Plan> getAllPlans() {
        return planRepo.findAll();
    }

    public Plan getByName(String name) {
        return planRepo.findByName(name).orElse(null);
    }

    public Plan save(Plan plan) {
        return planRepo.save(plan);
    }
}