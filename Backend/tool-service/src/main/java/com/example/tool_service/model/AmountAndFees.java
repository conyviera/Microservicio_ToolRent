package com.example.tool_service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AmountAndFees {

    private Long idTypeTool;
    private int replacementValue;
    private int dailyRate;
    private int debtRate;
}
