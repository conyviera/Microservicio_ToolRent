package com.example.reportsAndQueries_service.controller;

import com.example.reportsAndQueries_service.service.ReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/reports")

public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/active-loans")
    public ResponseEntity<?> getActiveLoans() {
        return ResponseEntity.ok(reportService.getActiveLoans());
    }

    @GetMapping("/overdue-clients")
    public ResponseEntity<?> getOverdue() {
        return ResponseEntity.ok(reportService.getClientsWithOverdueLoans());
    }

    @GetMapping("/tools-ranking")
    public ResponseEntity<Map<Long, Integer>> getRanking() {
        return ResponseEntity.ok(reportService.getToolRanking());
    }
}