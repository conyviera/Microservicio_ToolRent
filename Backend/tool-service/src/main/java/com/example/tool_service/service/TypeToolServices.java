package com.example.tool_service.service;

import com.example.tool_service.entity.TypeToolEntity;
import com.example.tool_service.model.AmountAndFees;
import com.example.tool_service.model.TypeToolResponse;
import com.example.tool_service.repository.ToolRepository; // <--- NUEVO IMPORT
import com.example.tool_service.repository.TypeToolRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TypeToolServices {

    private final TypeToolRepository typeToolRepo;
    private final ToolRepository toolRepo; // <--- 1. Inyectamos el repositorio de herramientas físicas
    private final RestTemplate restTemplate;

    // 2. Actualizamos el constructor
    public TypeToolServices(TypeToolRepository typeToolRepo, ToolRepository toolRepo, RestTemplate restTemplate) {
        this.typeToolRepo = typeToolRepo;
        this.toolRepo = toolRepo;
        this.restTemplate = restTemplate;
    }

    /**
     * Helper 1.1: Searches for a tool type by name. If it does not exist, it creates it.
     */
    public TypeToolEntity findOrCreateTypeTool(String name, String category) {
        Optional<TypeToolEntity> existingTypeTool = typeToolRepo.findByNameAndCategory(name, category);

        if (existingTypeTool.isPresent()) {
            return existingTypeTool.get();
        } else {
            if ((name == null) || (name.isEmpty())) {
                throw new IllegalStateException("El nombre de la herramienta es obligatoria.");
            }
            if ((category == null) || (category.isEmpty())) {
                throw new IllegalStateException("La categoría de la herramienta es obligatoria.");
            }

            TypeToolEntity newTypeTool = TypeToolEntity.builder()
                    .name(name)
                    .category(category)
                    .build();

            return typeToolRepo.save(newTypeTool);
        }
    }

    /**
     * Helper 1: Returns the tool type according to the id
     */
    public TypeToolEntity getTypeToolById(long idTypeTool) {
        return typeToolRepo.findById(idTypeTool)
                .orElseThrow(() -> new EntityNotFoundException("No se encontró el tipo de herramienta con id: " + idTypeTool));
    }

    /**
     * MAIN METHOD: Gets types + prices + REAL STOCK
     */
    public List<TypeToolResponse> getAllTypeToolsWithAmounts() {
        List<TypeToolEntity> typeTools = typeToolRepo.findAll();
        List<TypeToolResponse> responseList = new ArrayList<>();

        for (TypeToolEntity type : typeTools) {
            int daily = 0;
            int debt = 0;
            int replacement = 0;

            // 1. Obtener Precios (Microservicio Amounts)
            try {
                AmountAndFees amounts = restTemplate.getForObject(
                        "http://amounts-service/amounts/tariff/" + type.getIdTypeTool(),
                        AmountAndFees.class
                );

                if (amounts != null) {
                    daily = amounts.getDailyRate();
                    debt = amounts.getDebtRate();
                    replacement = amounts.getReplacementValue();
                }
            } catch (Exception e) {
                System.err.println("No se pudieron obtener tarifas para id: " + type.getIdTypeTool());
            }

            // 2. CALCULAR STOCK DINÁMICO (Microservicio Tool - Base de datos local)
            // Usamos toolRepo para contar cuántas herramientas físicas con este ID de tipo tienen estado "AVAILABLE"
            int realStock = toolRepo.countByIdtypeToolAndState(type.getIdTypeTool(), "AVAILABLE");

            TypeToolResponse dto = new TypeToolResponse(
                    type.getIdTypeTool(),
                    type.getName(),
                    type.getCategory(),
                    daily,
                    debt,
                    replacement,
                    realStock // <--- Pasamos el stock real calculado
            );

            responseList.add(dto);
        }

        return responseList;
    }

    /**
     * Helper 3: Returns all categories
     */
    public List<String> getAllTypeToolCategory (){
        return typeToolRepo.findDistinctCategory();
    }
}