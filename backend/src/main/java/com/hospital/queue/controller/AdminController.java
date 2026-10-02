package com.hospital.queue.controller;

import com.hospital.queue.dto.DashboardDTOs;
import com.hospital.queue.service.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AdminController {

    private final AnalyticsService analyticsService;

    @Autowired
    public AdminController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/analytics")
    public ResponseEntity<DashboardDTOs.AdminAnalyticsResponse> getAdminAnalytics() {
        DashboardDTOs.AdminAnalyticsResponse analytics = analyticsService.getAdminAnalytics();
        return ResponseEntity.ok(analytics);
    }
}
