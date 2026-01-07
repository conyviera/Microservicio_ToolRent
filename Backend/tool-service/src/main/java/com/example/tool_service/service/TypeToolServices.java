package com.example.tool_service.service;

import com.example.tool_service.entity.TypeToolEntity;
import com.example.tool_service.repository.TypeToolRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TypeToolServices {

    private final TypeToolRepository typeToolRepo;

    public TypeToolServices(TypeToolRepository typeToolRepo) {
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
     * Helper 2: Returns all tool types
     */
    public List<TypeToolEntity> getAllTypeTools () {
        List<TypeToolEntity> typeTools = typeToolRepo.findAll();
        return typeTools;
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
