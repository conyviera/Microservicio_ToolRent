package com.example.reportsAndQueries_service.model;
import lombok.Data;
import java.util.List;

@Data
public class KardexItem {
    private String typeMove;
    private List<Long> toolIds;
}