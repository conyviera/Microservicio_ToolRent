package com.example.loansAndRefunds_service.model;

import lombok.Data;

@Data
public class Debt {
    private Long loanId;
    private Long customerId;
    int amount;
    String reason; }
