package com.example.loansAndRefunds_service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoanResponse {
    private Long idLoan;

    private Customer customer;

    private List<Tool> tool;

    private LocalDate deliveryDate;
    private LocalDate returnDate;
    private int rentalAmount;
    private String state;
}