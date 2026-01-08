package com.example.tool_service.service;

import com.example.tool_service.entity.ToolEntity;
import com.example.tool_service.entity.TypeToolEntity;
import com.example.tool_service.model.AmountAndFees;
import com.example.tool_service.model.Kardex;
import com.example.tool_service.repository.ToolRepository;
import com.example.tool_service.repository.TypeToolRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ToolServices {

    private final TypeToolServices typeToolServices;
    private final ToolRepository toolRepo;
    private final RestTemplate restTemplate;
    private final TypeToolRepository typeToolRepo;

    @Autowired
    public ToolServices(TypeToolServices typeToolServices, ToolRepository toolRepo, RestTemplate restTemplate,  TypeToolRepository typeToolRepo) {
        this.typeToolServices = typeToolServices;
        this.toolRepo = toolRepo;
        this.restTemplate = restTemplate;
        this.typeToolRepo = typeToolRepo;
    }

    public ToolEntity registerTool(String name, String category, int replacementValue, int dailyRate, int debtRate) {
        TypeToolEntity typeTool = typeToolServices.findOrCreateTypeTool(name, category);

        AmountAndFees amount = new AmountAndFees(
                typeTool.getIdTypeTool(),
                replacementValue,
                dailyRate,
                debtRate
        );
        // MEJORA: Usamos String.class para evitar errores de deserialización
        restTemplate.postForObject("http://amounts-service/amounts/createAmounts", amount, String.class);

        ToolEntity newTool = ToolEntity.builder()
                .idtypeTool(typeTool.getIdTypeTool())
                .state("AVAILABLE")
                .build();

        return toolRepo.save(newTool);
    }

    public List<Long> registerLotTool(String name, String category, int replacementValue, int dailyRate, int debtRate, int amount) {
        List<Long> tools = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            ToolEntity tool = registerTool(name, category, replacementValue, dailyRate, debtRate);
            tools.add(tool.getIdTool());
        }

        Long fakeUserId = 999L;
        Kardex kardex = new Kardex("TOOL_REGISTER", null, fakeUserId, tools);

        // MEJORA: Usamos String.class
        restTemplate.postForEntity("http://kardexAndMovements-service/movements/registerMovement", kardex, String.class);

        return tools;
    }

    // --- MÉTODOS M2 ---

    public ToolEntity rentToolByType(Long typeToolId) {
        Optional<ToolEntity> toolOpt = toolRepo.findFirstByIdtypeToolAndState(typeToolId, "AVAILABLE");

        if (toolOpt.isEmpty()) {
            throw new IllegalStateException("No hay stock disponible para el tipo de herramienta: " + typeToolId);
        }

        ToolEntity tool = toolOpt.get();
        tool.setState("ON_LOAN");
        return toolRepo.save(tool); // El .save() aquí es crucial
    }

    public void updateToolStatus(Long toolId, String newStatus) {
        ToolEntity tool = toolRepo.findById(toolId)
                .orElseThrow(() -> new EntityNotFoundException("Herramienta no encontrada"));

        tool.setState(newStatus);
        toolRepo.save(tool); // El .save() aquí es crucial
    }

    // --- MÉTODOS DE BAJA ---

    public ToolEntity unsubscribeTool(ToolEntity tool) {
        tool.setState("DECOMMISSIONED");
        return toolRepo.save(tool);
    }

    public ToolEntity deactivateUnusedTool (Long idTool) {
        ToolEntity tool = toolRepo.findById(idTool)
                .orElseThrow(() -> new EntityNotFoundException("Herramienta no encontrada: " + idTool));

        if (tool.getState().equals("AVAILABLE")) {
            return unsubscribeTool(tool);
        }
        throw new IllegalStateException("La herramienta está en uso o no disponible (Estado: " + tool.getState() + ")");
    }

    public ToolEntity discardDamagedTools (Long idTool) {
        ToolEntity tool = toolRepo.findById(idTool)
                .orElseThrow(() -> new EntityNotFoundException ("Herramienta no encontrada: " + idTool));

        // MEJORA CRÍTICA: Aceptamos "DAMAGED" también.
        // Esto arregla el problema de sincronización si M2 envió "DAMAGED" en lugar de "UNDER_REPAIR".
        if ("UNDER_REPAIR".equals(tool.getState()) || "DAMAGED".equals(tool.getState())) {
            return unsubscribeTool(tool);
        }

        throw new IllegalStateException("La herramienta no está en estado de reparación o daño (Estado actual: " + tool.getState() + ")");
    }

    /**
     * Utility 2: Search for all tools of the same type
     * @param idTypeTool
     * @return
     */
    public List<ToolEntity> findAllByTypeTool(Long idTypeTool) {

        // Verificamos si existe el tipo (opcional, pero buena práctica)
        if (!typeToolRepo.existsById(idTypeTool)) {
            throw new EntityNotFoundException("El Tipo de Herramienta con ID " + idTypeTool + " no existe.");
        }

        return toolRepo.findAllByIdtypeTool(idTypeTool);
    }
}