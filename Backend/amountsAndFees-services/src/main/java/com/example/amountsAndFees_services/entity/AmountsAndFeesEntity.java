package com.example.amountsAndFees_services.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AmountsAndFeesEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true)
    private Long idAmount;

    private Long idTypeTool;

    private int replacementValue;

    private int dailyRate;

    @Column(nullable = false)
    private int debtRate;


}
