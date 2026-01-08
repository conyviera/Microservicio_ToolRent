package com.example.loansAndRefunds_service.service;

import com.example.loansAndRefunds_service.entity.LoanEntity;
import com.example.loansAndRefunds_service.model.*;
import com.example.loansAndRefunds_service.repository.LoanRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class LoanService {

    private final LoanRepository loanRepo;
    private final DebtsService debtsService;
    private final RestTemplate restTemplate;

    @Autowired
    public LoanService(LoanRepository loanRepo, DebtsService debtsService, RestTemplate restTemplate) {
        this.loanRepo = loanRepo;
        this.debtsService = debtsService;
        this.restTemplate = restTemplate;
    }

    /**
     * RF2.1: Registrar Préstamo
     */
    @Transactional
    public LoanEntity createLoan(List<Long> typeToolIds, String customerRut, LocalDate deliveryDate, LocalDate returnDate) {

        if (returnDate.isBefore(deliveryDate)) {
            throw new IllegalArgumentException("Fecha de retorno inválida (anterior a entrega)");
        }

        Customer customer = getCustomerByRut(customerRut);
        validateCustomerForLoan(customer);

        LoanEntity loan = new LoanEntity();
        loan.setIdCustomer(customer.getId());
        loan.setDeliveryDate(deliveryDate);
        loan.setReturnDate(returnDate);
        loan.setState("ACTIVE");

        List<Long> reservedToolIds = new ArrayList<>();
        int totalRentalAmount = 0;

        for (Long typeId : typeToolIds) {
            Tariff tariff = getTariffByType(typeId);
            long days = ChronoUnit.DAYS.between(deliveryDate, returnDate);
            if (days == 0) days = 1;

            totalRentalAmount += (tariff.getDailyRate() * days);

            Tool reservedTool = reserveToolInInventory(typeId);
            reservedToolIds.add(reservedTool.getIdTool());
        }

        loan.setRentalAmount(totalRentalAmount);
        loan.setIdTool(reservedToolIds);

        LoanEntity savedLoan = loanRepo.save(loan);

        Long fakeUserId = 15L;
        registerKardexMovement("PRESTAMO", savedLoan.getIdLoan(), fakeUserId, reservedToolIds);

        return savedLoan;
    }

    /**
     * RF2.3: Registrar Devolución
     */
    @Transactional
    public LoanEntity toolReturn(Long loanId, Map<Long, String> toolStates) {
        LoanEntity loan = loanRepo.findById(loanId)
                .orElseThrow(() -> new EntityNotFoundException("Préstamo no encontrado"));

        if ("RETURNED".equals(loan.getState())) {
            throw new IllegalArgumentException("El préstamo ya fue devuelto");
        }

        LocalDate today = LocalDate.now();
        List<Long> toolIds = loan.getIdTool();
        int totalFineAmount = 0;
        List<Long> toolsWithArrears = new ArrayList<>();

        long delayDays = ChronoUnit.DAYS.between(loan.getReturnDate(), today);
        boolean isLate = delayDays > 0;

        for (Map.Entry<Long, String> entry : toolStates.entrySet()) {
            Long toolId = entry.getKey();
            String newState = entry.getValue();

            if (!toolIds.contains(toolId)) continue;

            // ... dentro del bucle for (Map.Entry<Long, String> entry : toolStates.entrySet()) ...

            // 1. Obtener info de la herramienta
            Tool toolInfo = getToolFromInventory(toolId);
            Tariff tariff = getTariffByType(toolInfo.getIdTypeTool());

            // --- CORRECCIÓN DE ESTADOS AQUÍ ---
            String statusToSend = newState; // Por defecto

            if ("GOOD".equalsIgnoreCase(newState)) {
                // Si está buena, vuelve a estar DISPONIBLE para que el stock la cuente
                statusToSend = "AVAILABLE";
            }
            else if ("DAMAGED".equalsIgnoreCase(newState)) {
                // Si está dañada, va a REPARACIÓN (no cuenta en stock hasta que se evalúe)
                statusToSend = "UNDER_REPAIR";
            }

            System.out.println(">>> DEBUG: Actualizando herramienta " + toolId + " a estado: " + statusToSend);
            updateToolStatusInInventory(toolId, statusToSend);

            // ... (el resto del código de multas sigue igual) ...
        }

        if (isLate && totalFineAmount > 0) {
            debtsService.createDebt(loan, "ATRASO", totalFineAmount, toolsWithArrears);
        }

        loan.setState("RETURNED");
        LoanEntity savedLoan = loanRepo.save(loan);

        registerKardexMovement("DEVOLUCION", savedLoan.getIdLoan(), loan.getIdCustomer(), toolIds);

        return savedLoan;
    }

    // --- Helpers ---

    private void validateCustomerForLoan(Customer customer) {
        if (!"ACTIVE".equalsIgnoreCase(customer.getState())) {
            throw new IllegalArgumentException("Cliente restringido");
        }
        if (debtsService.hasPendingDebts(customer.getId())) {
            throw new IllegalArgumentException("Cliente tiene deudas pendientes");
        }
        int activeLoans = loanRepo.countByIdCustomerAndState(customer.getId(), "ACTIVE");
        if (activeLoans >= 5) {
            throw new IllegalArgumentException("Cliente excede límite de préstamos");
        }
    }

    private Customer getCustomerByRut(String rut) {
        return restTemplate.getForObject("http://customer-service/customers/byRut/" + rut, Customer.class);
    }

    private Tariff getTariffByType(Long typeToolId) {
        return restTemplate.getForObject("http://amounts-service/amounts/tariff/" + typeToolId, Tariff.class);
    }

    private Tool reserveToolInInventory(Long typeToolId) {
        return restTemplate.postForObject("http://inventory-service/tools/rent/" + typeToolId, null, Tool.class);
    }

    private void updateToolStatusInInventory(Long toolId, String newState) {
        restTemplate.put("http://inventory-service/tools/" + toolId + "/status?newStatus=" + newState, null);
    }

    private Tool getToolFromInventory(Long toolId) {
        return restTemplate.getForObject("http://inventory-service/tools/" + toolId, Tool.class);
    }

    private void registerKardexMovement(String type, Long loanId, Long userId, List<Long> toolIds) {
        Map<String, Object> req = new HashMap<>();
        req.put("typeMove", type);
        req.put("loanId", loanId);
        req.put("userId", userId);
        req.put("toolIds", toolIds);
        restTemplate.postForObject("http://kardexAndMovements-service/movements/registerMovement", req, String.class);
    }

    public List<LoanEntity> getActiveLoans() {
        return loanRepo.findByStateIsNot("RETURNED");
    }

    public int RentalAmount(List<Long> typeToolIds, LocalDate deliveryDate, LocalDate returnDate) {
        int totalRentalAmount = 0;
        for (Long typeId : typeToolIds) {
            Tariff tariff = getTariffByType(typeId);
            long days = ChronoUnit.DAYS.between(deliveryDate, returnDate);
            if (days == 0) days = 1;
            totalRentalAmount += (tariff.getDailyRate() * days);
        }
        return totalRentalAmount;
    }

    // --- MÉTODOS CORREGIDOS ---

    public List<LoanResponse> getAllLoansWithDetails() {
        List<LoanEntity> loans = loanRepo.findAll();
        List<LoanResponse> responseList = new ArrayList<>();

        for (LoanEntity loan : loans) {
            // 1. Obtener Cliente
            Customer customerObj = new Customer();
            try {
                customerObj = restTemplate.getForObject(
                        "http://customer-service/customers/" + loan.getIdCustomer(),
                        Customer.class
                );
            } catch (Exception e) {
                customerObj.setId(loan.getIdCustomer());
                customerObj.setName("Desconocido");
            }

            // 2. Obtener Herramientas (Lógica agregada para la lista)
            List<Tool> toolList = new ArrayList<>();
            if (loan.getIdTool() != null) {
                for (Long toolId : loan.getIdTool()) {
                    try {
                        Tool tool = getToolFromInventory(toolId); // Usamos el helper existente
                        if (tool != null) {
                            if (tool.getTypeTool() == null) {
                                tool.setTypeTool(new TypeTool());
                            }
                            try {
                                TypeTool typeInfo = restTemplate.getForObject(
                                        "http://inventory-service/tools/getTypeToolById/" + tool.getIdTypeTool(),
                                        TypeTool.class
                                );
                                if (typeInfo != null) {
                                    tool.getTypeTool().setName(typeInfo.getName());
                                    tool.getTypeTool().setIdTypeTool(typeInfo.getIdTypeTool());
                                }
                            } catch (Exception e) {
                                tool.getTypeTool().setName("Herramienta #" + tool.getIdTypeTool());
                            }
                            toolList.add(tool);
                        }
                    } catch (Exception e) {
                        // Ignorar errores puntuales en lista masiva
                    }
                }
            }

            // 3. Armar respuesta
            LoanResponse dto = new LoanResponse();
            dto.setIdLoan(loan.getIdLoan());
            dto.setCustomer(customerObj);
            dto.setTool(toolList); // <--- Ahora sí enviamos la lista llena
            dto.setDeliveryDate(loan.getDeliveryDate());
            dto.setReturnDate(loan.getReturnDate());
            dto.setRentalAmount(loan.getRentalAmount());
            dto.setState(loan.getState());

            responseList.add(dto);
        }
        return responseList;
    }

    public LoanResponse getLoanByIdWithDetails(Long id) {
        LoanEntity loan = loanRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Préstamo no encontrado"));

        Customer customerObj = new Customer();
        try {
            customerObj = restTemplate.getForObject(
                    "http://customer-service/customers/" + loan.getIdCustomer(),
                    Customer.class
            );
        } catch (Exception e) {
            customerObj.setId(loan.getIdCustomer());
            customerObj.setName("Desconocido");
        }

        List<Tool> toolList = new ArrayList<>();
        if (loan.getIdTool() != null) {
            for (Long toolId : loan.getIdTool()) {
                try {
                    Tool tool = getToolFromInventory(toolId);
                    if (tool != null) {
                        // AQUÍ ESTABA EL ERROR: Inicializar TypeTool si es null
                        if (tool.getTypeTool() == null) {
                            tool.setTypeTool(new TypeTool());
                        }

                        try {
                            // CORRECCIÓN: Usamos getIdTypeTool() o getIdtypeTool()
                            Long typeId = tool.getIdTypeTool();

                            TypeTool typeInfo = restTemplate.getForObject(
                                    "http://inventory-service/tools/getTypeToolById/" + typeId,
                                    TypeTool.class
                            );

                            if (typeInfo != null) {
                                tool.getTypeTool().setName(typeInfo.getName());
                                tool.getTypeTool().setIdTypeTool(typeInfo.getIdTypeTool());
                            } else {
                                tool.getTypeTool().setName("Herramienta #" + typeId);
                            }
                        } catch (Exception e) {
                            tool.getTypeTool().setName("Desconocido");
                        }
                        toolList.add(tool);
                    }
                } catch (Exception e) {
                    System.err.println("Error tool " + toolId);
                }
            }
        }

        LoanResponse dto = new LoanResponse();
        dto.setIdLoan(loan.getIdLoan());
        dto.setCustomer(customerObj);
        dto.setTool(toolList);
        dto.setDeliveryDate(loan.getDeliveryDate());
        dto.setReturnDate(loan.getReturnDate());
        dto.setRentalAmount(loan.getRentalAmount());
        dto.setState(loan.getState());

        return dto;
    }
}