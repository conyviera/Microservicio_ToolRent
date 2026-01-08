package com.example.tool_service.service;

import com.example.tool_service.entity.TypeToolEntity;
import com.example.tool_service.model.AmountAndFees;
import com.example.tool_service.model.TypeToolResponse;
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
    private final RestTemplate restTemplate;

    public TypeToolServices(TypeToolRepository typeToolRepo,  RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.typeToolRepo = typeToolRepo;
    }


    /**
     * Helper 1.1: Searches for a tool type by name. If it does not exist, it creates it. This prevents duplicate types in the database.
     * @param name
     * @param category
     * @return
     */
    public TypeToolEntity findOrCreateTypeTool(String name, String category) {

        Optional<TypeToolEntity> existingTypeTool =
                typeToolRepo.findByNameAndCategory (name, category);

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
     * @param idTypeTool
     * @return
     */
    public TypeToolEntity getTypeToolById(long idTypeTool) {

        TypeToolEntity typeTool = typeToolRepo.findById(idTypeTool)
                .orElseThrow(() -> new EntityNotFoundException("No se encontró el tipo de herramienta con id: " + idTypeTool));

        return typeTool;
    }

    /**
     *
     * @return
     */

    public List<TypeToolResponse> getAllTypeToolsWithAmounts() {
        List<TypeToolEntity> typeTools = typeToolRepo.findAll();
        List<TypeToolResponse> responseList = new ArrayList<>();

        for (TypeToolEntity type : typeTools) {
            int daily = 0;
            int debt = 0;
            int replacement = 0;

            try {
                // USAMOS TU FUNCIÓN EXISTENTE: /amounts/tariff/{id}
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
                // Loguear error pero no detener el flujo
                System.err.println("No se pudieron obtener tarifas para id: " + type.getIdTypeTool());
            }

            int stock = typeToolRepo.countAvailableTools(type.getIdTypeTool());

            TypeToolResponse dto = new TypeToolResponse(
                    type.getIdTypeTool(),
                    type.getName(),
                    type.getCategory(),
                    daily,
                    debt,
                    replacement,
                    stock
            );

            responseList.add(dto);
        }

        return responseList;
    }

    /**
     * Helper 3: Returns all categories
     * @return
     */
    public List<String> getAllTypeToolCategory (){
        List<String> category= typeToolRepo.findDistinctCategory();

        return category;
    }



}
