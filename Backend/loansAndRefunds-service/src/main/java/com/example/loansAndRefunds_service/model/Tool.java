package com.example.loansAndRefunds_service.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.Map;

@Data
public class Tool {
    private Long idTool;
    private String state;

    @JsonProperty("idtypeTool")
    private Long idTypeTool;
    // El objeto anidado que espera el frontend
    private TypeTool typeTool;
}