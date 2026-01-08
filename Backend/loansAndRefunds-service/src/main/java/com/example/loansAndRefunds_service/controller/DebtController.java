package com.example.loansAndRefunds_service.controller;

import com.example.loansAndRefunds_service.entity.DebtsEntity;
import com.example.loansAndRefunds_service.model.DebtResponse;
import com.example.loansAndRefunds_service.repository.DebtsRepository;
import com.example.loansAndRefunds_service.service.DebtsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/debts")

public class DebtController {

    private final DebtsService debtsService;
    private final DebtsRepository debtsRepo;

    public DebtController(DebtsService debtsService,  DebtsRepository debtsRepo) {
        this.debtsService = debtsService;
        this.debtsRepo = debtsRepo;
    }

    @PostMapping("/{debtId}/pay")
    public ResponseEntity<DebtResponse> payDebt(@PathVariable Long debtId) { // <--- Usa DebtResponse
        return ResponseEntity.ok(debtsService.payDebt(debtId));
    }

    // Evaluar daño de herramienta (Assessment)
    @PostMapping("/{debtId}/assess")
    public ResponseEntity<?> assessDebt(@PathVariable Long debtId, @RequestBody Map<String, Object> payload) {
        try {
            String outcome = (String) payload.get("outcome");

            // Asegúrate de manejar el cast de 'cost' de forma segura
            int cost = 0;
            if (payload.get("cost") != null) {
                cost = ((Number) payload.get("cost")).intValue();
            }

            // Ahora devuelve DebtResponse, que es seguro para JSON
            return ResponseEntity.ok(debtsService.assessDamage(debtId, outcome, cost));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al evaluar: " + e.getMessage());
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<DebtResponse>> getAllDebts() {
        // Usamos el servicio que devuelve el DTO limpio
        return ResponseEntity.ok(debtsService.getAllDebtsAsResponse());
    }

    @GetMapping("/loan/{idLoan}")
    public ResponseEntity<List<DebtResponse>> getDebtsByLoanId(@PathVariable Long idLoan) {
        // Usamos el método con detalles
        List<DebtResponse> debts = debtsService.getDebtsByIdLoanWithDetails(idLoan);
        return ResponseEntity.ok(debts);
    }
}