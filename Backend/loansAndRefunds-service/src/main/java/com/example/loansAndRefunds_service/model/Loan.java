package com.example.loansAndRefunds_service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Map; // <--- Importante para toolStates

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Loan {
    // Datos para solicitar préstamo (Creación)
    private List<Long> typeToolIds;
    private String customerRut;
    private LocalDate deliveryDate;
    private LocalDate returnDate;

    // Datos para devolución (Return)
    private Long loanId;
    private Map<Long, String> toolStates; // Key: toolId, Value: Estado ("OK", "DAMAGED", etc.)
}