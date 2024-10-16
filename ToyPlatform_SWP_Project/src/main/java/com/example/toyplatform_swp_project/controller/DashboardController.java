package com.example.toyplatform_swp_project.controller;

import com.example.toyplatform_swp_project.services.implement.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/overview")
    public Map<String, Object> getDashboardOverview() {
        Map<String, Object> response = new HashMap<>();
        response.put("totalUsers", dashboardService.getTotalUsers());
        response.put("totalOrders", dashboardService.getTotalOrders());
        response.put("totalRevenue", dashboardService.getTotalRevenue());

        return response;
    }
}
