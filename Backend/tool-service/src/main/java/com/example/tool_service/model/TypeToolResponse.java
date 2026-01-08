package com.example.tool_service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TypeToolResponse {
    private Long idTypeTool;
    private String name;
    private String category;

    private int dailyRate;
    private int debtRate;
    private int replacementValue;

    private int stock;
}
