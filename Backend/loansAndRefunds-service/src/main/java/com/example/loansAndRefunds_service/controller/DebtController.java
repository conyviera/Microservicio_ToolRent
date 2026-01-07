package com.example.loansAndRefunds_service.controller;

import com.example.loansAndRefunds_service.entity.DebtsEntity;
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

    // Pagar una deuda
    @PostMapping("/{debtId}/pay")
    public ResponseEntity<DebtsEntity> payDebt(@PathVariable Long debtId) {
        return ResponseEntity.ok(debtsService.payDebt(debtId));
    }

    // Evaluar daño de herramienta (Assessment)
    @PostMapping("/{debtId}/assess")
    public ResponseEntity<?> assessDebt(@PathVariable Long debtId, @RequestBody Map<String, Object> payload) {
        try {
            String outcome = (String) payload.get("outcome"); // "IRREPARABLE" o "MINOR_DAMAGE"
            int cost = (int) payload.get("cost"); // Costo de reposición o reparación

            return ResponseEntity.ok(debtsService.assessDamage(debtId, outcome, cost));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllDebts() {
        return ResponseEntity.ok(debtsRepo.findAll());
    }

    @GetMapping("/loan/{idLoan}")
    public ResponseEntity<List<DebtsEntity>> getDebtsByLoanId(@PathVariable Long idLoan) {
        List<DebtsEntity> debts = debtsService.getDebtsByIdLoan(idLoan);
        return ResponseEntity.ok(debts);
    }
}