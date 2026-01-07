package com.example.kardexAndMovements_service.entity; // Paquete de M5

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KardexEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idKardex;

    private String typeMove; // PRESTAMO, DEVOLUCION, BAJA, etc.

    private LocalDateTime date;


    private int quantity;

    @Column(name = "loan_id")
    private Long loanId;

    @Column(name = "user_id")
    private Long userId;

    @OneToMany(
            mappedBy = "kardex",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )

    @Builder.Default
    private List<KardexDetailEntity> kardexDetail = new ArrayList<>();


    public void addDetail(KardexDetailEntity detail) {
        kardexDetail.add(detail);
        detail.setKardex(this);
    }
}