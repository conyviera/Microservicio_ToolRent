package com.example.tool_service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Kardex {
    private String typeMove;
    private Long loanId;
    private Long userId;        // <--- ¡AGREGA ESTO!
    private List<Long> toolIds;
}