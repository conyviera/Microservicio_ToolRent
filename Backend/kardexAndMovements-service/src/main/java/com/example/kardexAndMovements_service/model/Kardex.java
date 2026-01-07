package com.example.kardexAndMovements_service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor // Para poder hacer new Kardex(tipo, id, lista)
@NoArgsConstructor
public class Kardex {
    private String typeMove;      // "PRESTAMO", "BAJA", etc.
    private Long loanId;          // Puede ser null o una Baja o Registro nuevo
    private List<Long> toolIds;   // Lista de IDs de herramientas afectadas
    private Long userId;
}