package com.example.loansAndRefunds_service.controller;

import com.example.loansAndRefunds_service.entity.LoanEntity;
import com.example.loansAndRefunds_service.model.Loan;
import com.example.loansAndRefunds_service.model.LoanResponse;
import com.example.loansAndRefunds_service.repository.LoanRepository;
import com.example.loansAndRefunds_service.service.LoanService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/loans")

public class LoanController {

    private final LoanService loanService;
    private final LoanRepository loanRepo;

    @Autowired
    public LoanController(LoanService loanService,  LoanRepository loanRepo) {
        this.loanService = loanService;
        this.loanRepo = loanRepo;
    }

    /**
     * RF2.1: Registrar un préstamo
     * Endpoint: POST /loans/create
     */
    @PostMapping("/create")
    public ResponseEntity<?> createLoan(@RequestBody Loan request) {
        try {
            LoanEntity loan = loanService.createLoan(
                    request.getTypeToolIds(),
                    request.getCustomerRut(),
                    request.getDeliveryDate(),
                    request.getReturnDate()
            );
            return ResponseEntity.ok(loan);
        } catch (IllegalArgumentException e) {
            // Retorna 400 Bad Request si fallan las validaciones de negocio
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al procesar el préstamo: " + e.getMessage());
        }
    }

    /**
     * RF2.3: Registrar devolución
     * Endpoint: POST /loans/return
     */
    @PostMapping("/return")
    public ResponseEntity<?> returnLoan(@RequestBody Loan request) {
        try {
            LoanEntity loan = loanService.toolReturn(
                    request.getLoanId(),
                    request.getToolStates()
            );
            return ResponseEntity.ok(loan);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al procesar la devolución: " + e.getMessage());
        }
    }

    /**
     * RF6.1: Listar préstamos activos
     * Endpoint: GET /loans/active
     */
    @GetMapping("/active")
    public ResponseEntity<List<LoanEntity>> getActiveLoans() {
        List<LoanEntity> loans = loanService.getActiveLoans();
        if (loans.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(loans);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LoanResponse> getLoanById(@PathVariable("id") Long loanId) {
        try {
            // Usamos el nuevo método que devuelve el DTO completo
            LoanResponse loan = loanService.getLoanByIdWithDetails(loanId);
            return ResponseEntity.ok(loan);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Cambia el tipo de retorno de LoanEntity a LoanResponse
    @GetMapping("/getAllLoans")
    public ResponseEntity<List<LoanResponse>> getAllLoans(){
        // Usamos el nuevo método con detalles
        List<LoanResponse> loans = loanService.getAllLoansWithDetails();
        return ResponseEntity.ok(loans);
    }

    @PostMapping("/RentalAmount")
    public ResponseEntity<?> RentalAmount (@RequestBody Map<String, Object> payload){
        try {
            List<Long> typeToolIds = ((List<?>) payload.get("typeToolIds")).stream()
                    .map(id -> ((Number) id).longValue())
                    .collect(Collectors.toList());

            LocalDate deliveryDate = LocalDate.parse((String) payload.get("deliveryDate"));
            LocalDate returnDate = LocalDate.parse((String) payload.get("returnDate"));

            int amount= loanService.RentalAmount(typeToolIds, deliveryDate, returnDate);

            return ResponseEntity.status(HttpStatus.CREATED).body(amount);

        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());

        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ocurrió un error al procesar " + e.getMessage());
        }

    }



}