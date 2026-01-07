package com.example.tool_service.entity;

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
public class ToolEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true)
    private Long idTool;

    private Long idtypeTool;

    /**
     * State:
     * AVAILABLE (Disponible)
     * ON_LOAN (Prestada)
     * UNDER_REPAIR (En reparación)
     * DECOMMISSIONED (Dada de baja)
     */
    private String state;
}
