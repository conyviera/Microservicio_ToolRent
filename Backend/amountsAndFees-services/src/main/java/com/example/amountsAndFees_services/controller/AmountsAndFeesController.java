package com.example.amountsAndFees_services.controller;

import com.example.amountsAndFees_services.entity.AmountsAndFeesEntity;
import com.example.amountsAndFees_services.model.AmountAndFees;
import com.example.amountsAndFees_services.service.AmountsAndFeesServices;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/amounts")
public class AmountsAndFeesController {

    private final AmountsAndFeesServices amountsService;

    public AmountsAndFeesController(AmountsAndFeesServices amountsService) {
        this.amountsService = amountsService;
    }

    /**
     * Register amount and fees
     */
    @PostMapping("/createAmounts")
    public ResponseEntity<AmountsAndFeesEntity> createAmounts(@RequestBody AmountAndFees amount) {

        AmountsAndFeesEntity saved = amountsService.registerAmountAndFees(
                amount.getIdTypeTool(),
                amount.getReplacementValue(),
                amount.getDailyRate(),
                amount.getDebtRate()
        );
        return ResponseEntity.ok(saved);
    }

    /**
     * RF4.1 Set daily rental rate
     */
    @PutMapping("/configurationDailyRateTypeTool/{idTypeTool}")
    public ResponseEntity<?> configurationDailyRateTypeTool(@PathVariable Long idTypeTool, @RequestBody Map<String, Object> payload) {
        try {
            int dailyRate = payload.get("dailyRate") != null ? ((Number) payload.get("dailyRate")).intValue() : -1;

            return ResponseEntity.ok(amountsService.setDailyRentalRate(idTypeTool, dailyRate));

        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error interno: " + e.getMessage());
        }
    }

    /**
     * RF4.2 Set daily late fee rate
     */
    @PutMapping("/setDebtRentalRate/{idTypeTool}")
    public ResponseEntity<?> setDebtRentalRate(@PathVariable Long idTypeTool, @RequestBody Map<String, Object> payload) {
        try {
            int debtRate = payload.get("debtRate") != null ? ((Number) payload.get("debtRate")).intValue() : -1;

            return ResponseEntity.ok(amountsService.setDebtRentalRate(idTypeTool, debtRate));

        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error interno: " + e.getMessage());
        }
    }

    /**
     * RF4.3 Register the replacement cost of each tool
     */
    @PutMapping ("/registerReplacementValue/{idTypeTool}")
    public ResponseEntity<?> registerReplacementValue(@PathVariable Long idTypeTool, @RequestBody Map<String, Object> payload){
        try {
            int replacementValue = payload.get("replacementValue") != null ? ((Number) payload.get("replacementValue")).intValue() : -1;

            return ResponseEntity.ok(amountsService.registerReplacementValue(idTypeTool, replacementValue));

        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error interno: " + e.getMessage());
        }
    }

    /**
     * Endpoint usado por LoanService (M2) para calcular costos.
     * GET /amounts/tariff/{idTypeTool}
     */
    @GetMapping("/tariff/{idTypeTool}")
    public ResponseEntity<?> getTariffByType(@PathVariable Long idTypeTool) {
        try {
            AmountsAndFeesEntity amount = amountsService.getAmountByType(idTypeTool);
            return ResponseEntity.ok(amount);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }


}
