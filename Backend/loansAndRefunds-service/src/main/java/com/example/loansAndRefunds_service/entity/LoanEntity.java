package com.example.loansAndRefunds_service.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idLoan;

    private List<Long> idTool;

    private Long idCustomer;

    private LocalDate deliveryDate;

    private LocalDate returnDate;

    /**
     * State:
     * RETURNED (Devolución)
     * ACTIVE (Activo)
     * EXPIRED (Vencido)
     */
    private String state;

    private int rentalAmount;
}
