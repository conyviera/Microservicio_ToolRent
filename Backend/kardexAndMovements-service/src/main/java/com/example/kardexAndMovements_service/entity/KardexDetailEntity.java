package com.example.kardexAndMovements_service.entity;


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
public class KardexDetailEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idKardexDetail;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_kardex", nullable = false)
    private KardexEntity kardex;


    @Column(name = "tool_id", nullable = false)
    private Long toolId;
}