package com.example.loansAndRefunds_service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor // Para poder hacer new Kardex(tipo, id, lista)
@NoArgsConstructor
public class Kardex {
    private String typeMove;
    private Long loanId;
    private Long userId;        // <--- ¡AGREGA ESTO!
    private List<Long> toolIds;// Lista de IDs de herramientas afectadas
}

