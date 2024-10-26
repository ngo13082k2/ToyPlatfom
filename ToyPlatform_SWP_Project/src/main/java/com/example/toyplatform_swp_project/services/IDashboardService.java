package com.example.toyplatform_swp_project.services;

public interface IDashboardService {
    Long getTotalUsers();
    Long getTotalOrders();
    Double getTotalRevenue();
    Long getCompleteOrder();
}
