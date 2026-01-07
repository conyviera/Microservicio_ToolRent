package com.example.loansAndRefunds_service.model;

import lombok.Data;

@Data
public class Tariff {
    private Long idTypeTool;
    private int dailyRate;
    private int debtRate;
    private int replacementValue;
}