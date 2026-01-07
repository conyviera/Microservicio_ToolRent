package com.example.reportsAndQueries_service.model;
import lombok.Data;
import java.time.LocalDate;

@Data
public class LoanReport {
    private Long idLoan;
    private Long idCustomer;
    private LocalDate deliveryDate;
    private LocalDate returnDate;
    private String state;
}