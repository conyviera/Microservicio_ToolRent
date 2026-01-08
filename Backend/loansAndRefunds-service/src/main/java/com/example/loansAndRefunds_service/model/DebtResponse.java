package com.example.loansAndRefunds_service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.List; // <--- Importante

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DebtResponse {
    private Long idDebts;
    private double amount;
    private String type;
    private String status;
    private LocalDate creationDate;
    private LocalDate paymentDate;

    private Long idLoan;
    private Long idCustomer;

    // ESTE ES EL CAMPO QUE FALTA Y CAUSA EL ERROR
    private List<Tool> tool;
}