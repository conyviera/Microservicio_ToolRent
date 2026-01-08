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
public class DebtsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDebts;

    private double amount;

    /**
     * Type:
     * ARREARS (atraso)
     * DAMAGES (daño)
     */
    private String type;

    /**
     * Status:
     * PAID (pagada)
     * PENDING (pendiente)
     */
    private String status;

    @Column(nullable = false)
    private LocalDate creationDate;

    @Column(nullable = true)
    private LocalDate paymentDate;

    private Long idCustomer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_loan")
    private LoanEntity loan;

    @ElementCollection
    private List<Long> idTools;


}
