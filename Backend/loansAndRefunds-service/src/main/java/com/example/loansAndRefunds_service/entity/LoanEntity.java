package com.example.loansAndRefunds_service.entity;

import jakarta.persistence.*;
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

    @ElementCollection
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
