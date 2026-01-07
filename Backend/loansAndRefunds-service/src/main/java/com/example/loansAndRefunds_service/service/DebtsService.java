package com.example.loansAndRefunds_service.service;

import com.example.loansAndRefunds_service.entity.DebtsEntity;
import com.example.loansAndRefunds_service.entity.LoanEntity;
import com.example.loansAndRefunds_service.repository.DebtsRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;

@Service
public class DebtsService {

    private final DebtsRepository debtsRepo;
    private final RestTemplate restTemplate;

    @Autowired
    public DebtsService(DebtsRepository debtsRepo, RestTemplate restTemplate) {
        this.debtsRepo = debtsRepo;
        this.restTemplate = restTemplate;
    }

    @Transactional
    public DebtsEntity createDebt(LoanEntity loan, String type, int amount, List<Long> idTools) {
        DebtsEntity debt = new DebtsEntity();
        debt.setLoan(loan);
        debt.setIdCustomer(loan.getIdCustomer());
        debt.setAmount(amount);
        debt.setType(type);
        // Lógica: Si es "DAMAGE", queda en evaluación. Si es "ATRASO" (o cualquier otro), queda pendiente de pago.
        debt.setStatus("DAMAGE".equalsIgnoreCase(type) ? "PENDING_ASSESSMENT" : "PENDING");
        debt.setCreationDate(LocalDate.now());
        debt.setIdTools(idTools);

        DebtsEntity savedDebt = debtsRepo.save(debt);

        updateCustomerStateInM3(loan.getIdCustomer(), "RESTRICTED");

        return savedDebt;
    }

    @Transactional
    public DebtsEntity payDebt(Long debtId) {
        DebtsEntity debt = debtsRepo.findById(debtId)
                .orElseThrow(() -> new EntityNotFoundException("Deuda no encontrada: " + debtId));

        if ("PAID".equals(debt.getStatus())) {
            throw new IllegalStateException("La deuda ya está pagada");
        }

        debt.setStatus("PAID");
        debt.setPaymentDate(LocalDate.now());
        DebtsEntity savedDebt = debtsRepo.save(debt);

        refreshRemoteCustomerState(debt.getIdCustomer());

        return savedDebt;
    }

    @Transactional
    public DebtsEntity assessDamage(Long debtId, String outcome, int cost) {
        DebtsEntity debt = debtsRepo.findById(debtId)
                .orElseThrow(() -> new EntityNotFoundException("Deuda no encontrada"));

        if (!"PENDING_ASSESSMENT".equals(debt.getStatus())) {
            throw new IllegalStateException("Esta deuda no está pendiente de evaluación");
        }

        List<Long> toolIds = debt.getIdTools();

        if ("IRREPARABLE".equalsIgnoreCase(outcome)) {
            debt.setAmount(cost);
            debt.setStatus("PENDING"); // Se confirma la deuda para pago

            // Llamada a M1: Dar de baja
            for (Long toolId : toolIds) {
                // MEJORA: Usamos String.class para evitar errores si M1 devuelve texto
                restTemplate.patchForObject("http://inventory-service/tools/discard/" + toolId, null, String.class);
            }

        } else if ("MINOR_DAMAGE".equalsIgnoreCase(outcome)) {
            debt.setAmount(cost);
            debt.setStatus("PENDING"); // Se confirma la deuda por reparación

            // Llamada a M1: Volver a Disponible
            for (Long toolId : toolIds) {
                restTemplate.put("http://inventory-service/tools/" + toolId + "/status?newStatus=AVAILABLE", null);
            }
        } else {
            throw new IllegalArgumentException("Resultado inválido. Use 'IRREPARABLE' o 'MINOR_DAMAGE'");
        }

        return debtsRepo.save(debt);
    }

    public boolean hasPendingDebts(Long customerId) {
        return debtsRepo.existsByIdCustomerAndStatus(customerId, "PENDING") ||
                debtsRepo.existsByIdCustomerAndStatus(customerId, "PENDING_ASSESSMENT");
    }

    private void updateCustomerStateInM3(Long customerId, String newState) {
        try {
            // MEJORA: String.class evita el error "Error extracting response for type [class java.lang.Void]"
            restTemplate.put(
                    "http://customer-service/customers/internal/state/" + customerId + "?state=" + newState,
                    null
            );
        } catch (Exception e) {
            System.err.println("Error comunicando con M3: " + e.getMessage());
        }
    }

    private void refreshRemoteCustomerState(Long customerId) {
        if (!hasPendingDebts(customerId)) {
            updateCustomerStateInM3(customerId, "ACTIVE");
        }
    }

    /**
     * Search for debts by loan ID
     */

    public List<DebtsEntity> getDebtsByIdLoan(Long idLoan) {
        return debtsRepo.findByLoan_IdLoan(idLoan);
    }
}