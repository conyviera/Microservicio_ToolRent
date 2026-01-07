package com.example.loansAndRefunds_service.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.Map;

@Data
public class Tool {

    private Long idTool;
    private String state;

    // Este es el campo que usaremos en la lógica
    private Long typeToolId;

    /**
     * TRUCO DE JACKSON:
     * El JSON viene como: "typeTool": { "idTypeTool": 1, ... }
     * Este método "atrapa" ese objeto y saca solo el ID para guardarlo en nuestra variable.
     */
    @JsonProperty("typeTool")
    private void unpackNested(Map<String, Object> typeTool) {
        if (typeTool != null) {
            // Buscamos "idTypeTool" o "id_type_tool" o como venga en el JSON
            if (typeTool.containsKey("idTypeTool")) {
                this.typeToolId = ((Number) typeTool.get("idTypeTool")).longValue();
            }
            else if (typeTool.containsKey("id")) {
                this.typeToolId = ((Number) typeTool.get("id")).longValue();
            }
        }
    }

    // Plan B: Por si el inventario alguna vez lo manda plano con otro nombre
    @JsonProperty("idtypeTool")
    private void setFlatId(Long id) {
        this.typeToolId = id;
    }
}