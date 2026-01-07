package com.example.tool_service.controller;

import com.example.tool_service.entity.ToolEntity;
import com.example.tool_service.entity.TypeToolEntity;
import com.example.tool_service.repository.ToolRepository;
import com.example.tool_service.service.ToolServices;
import com.example.tool_service.service.TypeToolServices;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/tools")

public class ToolController {

    private final ToolServices toolService;
    private final TypeToolServices typeToolService;
    private final ToolRepository toolRepo;

    public ToolController(ToolServices toolServices, ToolRepository toolRepo,  TypeToolServices typeToolService) {
        this.typeToolService = typeToolService;
        this.toolService = toolServices;
        this.toolRepo = toolRepo;
    }

    // --- RF1.1: Registro ---

    @PostMapping("/register")
    public ResponseEntity<?> registerTool(@RequestBody Map<String, Object> payload) {
        try {
            String name = (String) payload.get("name");
            String category = (String) payload.get("category");
            int replacementValue = getInt(payload, "replacementValue");
            int dailyRate = getInt(payload, "dailyRate");
            int debtRate = getInt(payload, "debtRate");

            ToolEntity savedTool = toolService.registerTool(name, category, replacementValue, dailyRate, debtRate);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedTool);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/registerBatch")
    public ResponseEntity<?> registerLotTool(@RequestBody Map<String, Object> payload) {
        try {
            String name = (String) payload.get("name");
            String category = (String) payload.get("category");
            int replacementValue = getInt(payload, "replacementValue");
            int dailyRate = getInt(payload, "dailyRate");
            int debtRate = getInt(payload, "debtRate");
            int amount = getInt(payload, "amount");

            List<Long> toolIds = toolService.registerLotTool(name, category, replacementValue, dailyRate, debtRate, amount);
            return ResponseEntity.status(HttpStatus.CREATED).body(toolIds);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    // --- RF 1.2: Bajas ---

    @PatchMapping("/deactivate/{id}")
    public ResponseEntity<?> deactivateUnusedTool(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(toolService.deactivateUnusedTool(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("/discard/{id}")
    public ResponseEntity<?> discardDamagedTools(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(toolService.discardDamagedTools(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // --- INTEGRACIÓN CON M2 (Préstamos) ---

    @PostMapping("/rent/{typeToolId}")
    public ResponseEntity<?> rentTool(@PathVariable Long typeToolId) {
        try {
            // Busca una, la pone en ON_LOAN y la retorna
            ToolEntity tool = toolService.rentToolByType(typeToolId);
            return ResponseEntity.ok(tool);
        } catch (IllegalStateException e) {
            // 409 Conflict si no hay stock
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PutMapping("/{toolId}/status")
    public ResponseEntity<?> updateToolStatus(@PathVariable Long toolId, @RequestParam String newStatus) {
        try {
            toolService.updateToolStatus(toolId, newStatus);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Helper para evitar nulos en el mapa
    private int getInt(Map<String, Object> map, String key) {
        return map.get(key) != null ? ((Number) map.get(key)).intValue() : 0;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ToolEntity> getToolById(@PathVariable Long id) {
        // Llama a tu repositorio findById...
        return toolRepo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Helper 1
     */
    @GetMapping("/getTypeToolById/{idTypeTool}")
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getTypeToolById(@PathVariable Long idTypeTool) {
        try {
            return ResponseEntity.ok(typeToolService.getTypeToolById(idTypeTool));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error interno: " + e.getMessage());
        }
    }

    /**
     * Helper 2
     */
    @GetMapping("/getAllTypeTools")
    //@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<List<TypeToolEntity>> getAllTool(){
        return ResponseEntity.ok(typeToolService.getAllTypeTools());
    }

    /**
     * Helper 3
     */
    @GetMapping("/getAllCategory")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<List<String>> getTypeToolCategory(){
        return ResponseEntity.ok(typeToolService.getAllTypeToolCategory());
    }
}