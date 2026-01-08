package com.example.loansAndRefunds_service.service;

import com.example.loansAndRefunds_service.entity.DebtsEntity;
import com.example.loansAndRefunds_service.entity.LoanEntity;
import com.example.loansAndRefunds_service.model.DebtResponse;
import com.example.loansAndRefunds_service.model.Tariff; // Asegúrate de tener este modelo
import com.example.loansAndRefunds_service.model.Tool;
import com.example.loansAndRefunds_service.model.TypeTool;
import com.example.loansAndRefunds_service.repository.DebtsRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
        // Si es DAMAGE queda pendiente de evaluación, si no, pendiente de pago
        debt.setStatus("DAMAGE".equalsIgnoreCase(type) ? "PENDING_ASSESSMENT" : "PENDING");
        debt.setCreationDate(LocalDate.now());
        debt.setIdTools(idTools);

        DebtsEntity savedDebt = debtsRepo.save(debt);

        updateCustomerStateInM3(loan.getIdCustomer(), "RESTRICTED");

        return savedDebt;
    }

    @Transactional
    public DebtResponse payDebt(Long debtId) {
        DebtsEntity debt = debtsRepo.findById(debtId)
                .orElseThrow(() -> new EntityNotFoundException("Deuda no encontrada: " + debtId));

        if ("PAID".equals(debt.getStatus())) {
            throw new IllegalStateException("La deuda ya está pagada");
        }

        debt.setStatus("PAID");
        debt.setPaymentDate(LocalDate.now());
        DebtsEntity savedDebt = debtsRepo.save(debt);

        refreshRemoteCustomerState(debt.getIdCustomer());

        // Retornamos DTO para evitar error 500
        return convertToDebtResponse(savedDebt);
    }

    // 1. Cambia el tipo de retorno de DebtsEntity a DebtResponse
    @Transactional
    public DebtResponse assessDamage(Long debtId, String outcome, int cost) {
        DebtsEntity debt = debtsRepo.findById(debtId)
                .orElseThrow(() -> new EntityNotFoundException("Deuda no encontrada"));

        if (!"PENDING_ASSESSMENT".equals(debt.getStatus())) {
            throw new IllegalStateException("Esta deuda no está pendiente de evaluación");
        }

        List<Long> toolIds = debt.getIdTools();
        int finalAmount = 0;

        if ("IRREPARABLE".equalsIgnoreCase(outcome)) {
            // ... (tu lógica de cálculo irreparable sigue igual) ...
            for (Long toolId : toolIds) {
                try {
                    Tool tool = getToolFromInventory(toolId);
                    if (tool != null) {
                        Tariff tariff = getTariffByType(tool.getIdTypeTool()); // Asegúrate que Tool.java tenga @JsonProperty
                        if (tariff != null) {
                            finalAmount += tariff.getReplacementValue();
                        }
                    }
                    restTemplate.patchForObject("http://inventory-service/tools/discard/" + toolId, null, String.class);
                } catch (Exception e) {
                    System.err.println("Error procesando herramienta: " + e.getMessage());
                    finalAmount += cost; // Fallback
                }
            }
            debt.setAmount(finalAmount);
            debt.setStatus("PENDING");

        } else if ("MINOR_DAMAGE".equalsIgnoreCase(outcome)) {
            // ... (tu lógica de daño menor sigue igual) ...
            debt.setAmount(cost);
            debt.setStatus("PENDING");

            for (Long toolId : toolIds) {
                try {
                    restTemplate.put("http://inventory-service/tools/" + toolId + "/status?newStatus=AVAILABLE", null);
                } catch (Exception e) {
                    System.err.println("Error al liberar herramienta: " + e.getMessage());
                }
            }
        } else {
            throw new IllegalArgumentException("Resultado inválido. Use 'IRREPARABLE' o 'MINOR_DAMAGE'");
        }

        DebtsEntity savedDebt = debtsRepo.save(debt);

        // 2. IMPORTANTE: Retorna el DTO convertido para evitar el error 500
        return convertToDebtResponse(savedDebt);
    }

    // --- Helpers de Comunicación (FALTABAN ESTOS) ---

    private Tool getToolFromInventory(Long toolId) {
        return restTemplate.getForObject("http://inventory-service/tools/" + toolId, Tool.class);
    }

    private Tariff getTariffByType(Long typeToolId) {
        return restTemplate.getForObject("http://amounts-service/amounts/tariff/" + typeToolId, Tariff.class);
    }

    private void updateCustomerStateInM3(Long customerId, String newState) {
        try {
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

    public boolean hasPendingDebts(Long customerId) {
        return debtsRepo.existsByIdCustomerAndStatus(customerId, "PENDING") ||
                debtsRepo.existsByIdCustomerAndStatus(customerId, "PENDING_ASSESSMENT");
    }

    public List<DebtsEntity> getDebtsByIdLoan(Long idLoan) {
        return debtsRepo.findByLoan_IdLoan(idLoan);
    }

    // --- Métodos de Conversión a DTO ---

    public List<DebtResponse> getDebtsByIdLoanWithDetails(Long idLoan) {
        List<DebtsEntity> debts = debtsRepo.findByLoan_IdLoan(idLoan);

        return debts.stream().map(debt -> {
            DebtResponse dto = convertToDebtResponse(debt);

            // Lógica para llenar nombres de herramientas (copiada y adaptada)
            List<Tool> toolList = new ArrayList<>();
            if (debt.getIdTools() != null) {
                for (Long toolId : debt.getIdTools()) {
                    try {
                        Tool tool = getToolFromInventory(toolId);
                        if (tool != null) {
                            if (tool.getTypeTool() == null) tool.setTypeTool(new TypeTool());

                            try {
                                TypeTool typeInfo = restTemplate.getForObject(
                                        "http://inventory-service/tools/getTypeToolById/" + tool.getIdTypeTool(),
                                        TypeTool.class
                                );
                                if (typeInfo != null) {
                                    tool.getTypeTool().setName(typeInfo.getName());
                                    tool.getTypeTool().setIdTypeTool(typeInfo.getIdTypeTool());
                                } else {
                                    tool.getTypeTool().setName("Herramienta #" + tool.getIdTypeTool());
                                }
                            } catch (Exception e) {
                                tool.getTypeTool().setName("Desconocido");
                            }
                            toolList.add(tool);
                        }
                    } catch (Exception e) {
                        // ignorar
                    }
                }
            }
            dto.setTool(toolList);
            return dto;
        }).collect(Collectors.toList());
    }

    public List<DebtResponse> getAllDebtsAsResponse() {
        return debtsRepo.findAll().stream()
                .map(this::convertToDebtResponse)
                .collect(Collectors.toList());
    }

    private DebtResponse convertToDebtResponse(DebtsEntity debt) {
        DebtResponse dto = new DebtResponse();
        dto.setIdDebts(debt.getIdDebts());
        dto.setAmount(debt.getAmount());
        dto.setType(debt.getType());
        dto.setStatus(debt.getStatus());
        dto.setCreationDate(debt.getCreationDate());
        dto.setPaymentDate(debt.getPaymentDate());
        dto.setIdCustomer(debt.getIdCustomer());
        if (debt.getLoan() != null) {
            dto.setIdLoan(debt.getLoan().getIdLoan());
        }
        return dto;
    }
}