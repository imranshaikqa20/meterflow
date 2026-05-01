package com.meterflow.controller;

import org.springframework.web.bind.annotation.*;
import java.util.List;

import com.meterflow.model.Plan;
import com.meterflow.service.PlanService;

@RestController
@RequestMapping("/plans")
@CrossOrigin("*")
public class PlanController {

    private final PlanService planService;

    public PlanController(PlanService planService) {
        this.planService = planService;
    }

    //  Get all plans
    @GetMapping
    public List<Plan> getPlans() {
        return planService.getAllPlans();
    }

    // Create plan (for admin/testing)
    @PostMapping
    public Plan createPlan(@RequestBody Plan plan) {
        return planService.save(plan);
    }
}