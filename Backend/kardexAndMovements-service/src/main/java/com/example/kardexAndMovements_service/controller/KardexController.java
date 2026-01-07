package com.example.kardexAndMovements_service.controller;

import com.example.kardexAndMovements_service.entity.KardexEntity;
import com.example.kardexAndMovements_service.model.Kardex;
import com.example.kardexAndMovements_service.service.KardexService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/movements")

public class KardexController {

    private final KardexService kardexService;

    public KardexController (KardexService kardexService) {
        this.kardexService = kardexService;
    }

    @PostMapping("/registerMovement")
    public ResponseEntity<?> registerMovement(@RequestBody Kardex kardexDto) {
        try {
            KardexEntity saved = kardexService.registerMovement(
                    kardexDto.getTypeMove(),
                    kardexDto.getLoanId(),
                    kardexDto.getUserId(), // <--- Pasamos el usuario que viene del JSON
                    kardexDto.getToolIds()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error interno: " + e.getMessage());
        }
    }

    /**
     * RF 5.2: Historial por herramienta
     */
    @GetMapping("/tool/{toolId}")
    public ResponseEntity<List<KardexEntity>> getHistoryByTool(@PathVariable Long toolId) {
        return ResponseEntity.ok(kardexService.getAllMovementsOfATool(toolId));
    }

    /**
     * RF 5.3: Historial por fechas
     * Ejemplo: /movements/range?start=2024-01-01T00:00:00&end=2024-12-31T23:59:59
     */
    @GetMapping("/range")
    public ResponseEntity<List<KardexEntity>> getHistoryByRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return ResponseEntity.ok(kardexService.listByDateRange(start, end));
    }

    /**
     *
     * @return
     */
    @GetMapping("/getAllMove")
    public ResponseEntity<List<KardexEntity>> getAllmove(){

        List<KardexEntity> kardexList= kardexService.findAllKardex();

        return ResponseEntity.ok(kardexList);
    }
}