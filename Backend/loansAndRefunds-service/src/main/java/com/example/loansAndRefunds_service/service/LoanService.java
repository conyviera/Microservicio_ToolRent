package com.example.loansAndRefunds_service.service;

import com.example.loansAndRefunds_service.entity.LoanEntity;
import com.example.loansAndRefunds_service.model.*;
import com.example.loansAndRefunds_service.repository.LoanRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.example.loansAndRefunds_service.model.LoanResponse;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class LoanService {

    private final LoanRepository loanRepo;
    private final DebtsService debtsService; // Inyección del servicio local de deudas
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

        // 1. Obtener Cliente desde M3
        Customer customer = getCustomerByRut(customerRut);

        // 2. Validar Cliente (RF 2.2 y RF 2.5) usando servicio local de deudas
        validateCustomerForLoan(customer);

        // 3. Crear entidad
        LoanEntity loan = new LoanEntity();
        loan.setIdCustomer(customer.getId());
        loan.setDeliveryDate(deliveryDate);
        loan.setReturnDate(returnDate);
        loan.setState("ACTIVE");

        List<Long> reservedToolIds = new ArrayList<>();
        int totalRentalAmount = 0;

        // 4. Procesar herramientas: Calcular precio (M4) y Reservar Stock (M1)
        for (Long typeId : typeToolIds) {
            // Llamada a M4 (Tarifas)
            Tariff tariff = getTariffByType(typeId);

            // RF 2.4: Calcular monto arriendo (Tarifa * Días)
            long days = ChronoUnit.DAYS.between(deliveryDate, returnDate);
            if (days == 0) days = 1; // Mínimo 1 día

            totalRentalAmount += (tariff.getDailyRate() * days);

            // Llamada a M1 (Inventario)
            Tool reservedTool = reserveToolInInventory(typeId);
            reservedToolIds.add(reservedTool.getIdTool());
        }

        loan.setRentalAmount(totalRentalAmount);
        loan.setIdTool(reservedToolIds);

        // 5. Guardar
        LoanEntity savedLoan = loanRepo.save(loan);

        // 6. Kardex (M5)
        Long fakeUserId = 15L;

        registerKardexMovement("PRESTAMO", savedLoan.getIdLoan(), fakeUserId, reservedToolIds);

        return savedLoan;
    }

    /**
     * RF2.3: Registrar Devolución
     */
    @Transactional
    public LoanEntity toolReturn(Long loanId, Map<Long, String> toolStates) {

        System.out.println("========== INICIO DEBUG DEVOLUCIÓN ==========");
        System.out.println(">>> DEBUG: Buscando préstamo ID: " + loanId);

        LoanEntity loan = loanRepo.findById(loanId)
                .orElseThrow(() -> new EntityNotFoundException("Préstamo no encontrado"));

        if ("RETURNED".equals(loan.getState())) {
            throw new IllegalArgumentException("El préstamo ya fue devuelto");
        }

        LocalDate today = LocalDate.now();
        List<Long> toolIds = loan.getIdTool();

        // -------------------------------------------------------------
        // PUNTO CRÍTICO 1: ¿Qué hay realmente en la base de datos?
        // Si esto imprime "[]" o "null", falta @ElementCollection en la Entidad
        System.out.println(">>> DEBUG: IDs de herramientas guardadas en este préstamo: " + toolIds);
        // -------------------------------------------------------------

        int totalFineAmount = 0;
        List<Long> damagedTools = new ArrayList<>();
        List<Long> toolsWithArrears = new ArrayList<>();

        long delayDays = ChronoUnit.DAYS.between(loan.getReturnDate(), today);
        boolean isLate = delayDays > 0;

        System.out.println(">>> DEBUG: IDs recibidos en el JSON: " + toolStates.keySet());

        for (Map.Entry<Long, String> entry : toolStates.entrySet()) {
            Long toolId = entry.getKey();
            String newState = entry.getValue();

            System.out.println("------------------------------------------------");
            System.out.println(">>> DEBUG: Procesando herramienta ID: " + toolId);

            // -------------------------------------------------------------
            // PUNTO CRÍTICO 2: La comparación
            // -------------------------------------------------------------
            if (!toolIds.contains(toolId)) {
                System.out.println(">>> ALERTA 🛑: El ID " + toolId + " NO ESTÁ en la lista del préstamo " + toolIds);
                System.out.println(">>> MOTIVO: Posible diferencia de tipos (Long vs Integer) o ID incorrecto.");
                continue;
            }

            System.out.println(">>> ÉXITO ✅: ID coincidente. Ejecutando lógica de actualización...");

            // 1. Obtener info de la herramienta
            Tool toolInfo = getToolFromInventory(toolId);
            Tariff tariff = getTariffByType(toolInfo.getTypeToolId());

            // 2. Actualizar estado físico en M1
            // FIX: Mapeamos DAMAGED a UNDER_REPAIR para que M1 no falle
            String statusToSend = newState;
            if ("DAMAGED".equalsIgnoreCase(newState)) {
                statusToSend = "UNDER_REPAIR";
                System.out.println(">>> DEBUG: Cambiando estado 'DAMAGED' a 'UNDER_REPAIR' para M1");
            }

            System.out.println(">>> DEBUG: Llamando a M1 (Inventario) para actualizar ID " + toolId + " a " + statusToSend);
            updateToolStatusInInventory(toolId, statusToSend);

            // 3. Calcular Multa por Atraso
            if (isLate) {
                totalFineAmount += (delayDays * tariff.getDebtRate());
                toolsWithArrears.add(toolId);
            }

            // 4. Calcular Multa por Daño
            if ("DAMAGED".equalsIgnoreCase(newState) || "DECOMMISSIONED".equalsIgnoreCase(newState)) {
                int damageFine = tariff.getReplacementValue();
                System.out.println(">>> DEBUG: Creando deuda por daño para herramienta " + toolId);
                debtsService.createDebt(loan, "DAMAGE", damageFine, List.of(toolId));
            }
        }

        System.out.println("------------------------------------------------");

        if (isLate && totalFineAmount > 0) {
            System.out.println(">>> DEBUG: Creando deuda por atraso.");
            debtsService.createDebt(loan, "ATRASO", totalFineAmount, toolsWithArrears);
        }

        loan.setState("RETURNED");
        LoanEntity savedLoan = loanRepo.save(loan);

        registerKardexMovement("DEVOLUCION", savedLoan.getIdLoan(), loan.getIdCustomer(), toolIds);

        System.out.println("========== FIN DEBUG DEVOLUCIÓN ==========");

        return savedLoan;
    }


    // =================================================================================
    //                     HELPERS E INTEGRACIONES
    // =================================================================================

    private void validateCustomerForLoan(Customer customer) {
        if (!"ACTIVE".equalsIgnoreCase(customer.getState())) {
            throw new IllegalArgumentException("Cliente restringido (Estado: " + customer.getState() + ")");
        }

        // Validación Local usando el servicio inyectado
        if (debtsService.hasPendingDebts(customer.getId())) {
            throw new IllegalArgumentException("Cliente tiene deudas pendientes");
        }

        int activeLoans = loanRepo.countByIdCustomerAndState(customer.getId(), "ACTIVE");
        if (activeLoans >= 5) {
            throw new IllegalArgumentException("Cliente excede límite de 5 préstamos activos");
        }
    }

    // --- M3 (CLIENTES) ---
    private Customer getCustomerByRut(String rut) {
        // Asegúrate que el nombre en Eureka sea 'customer-service'
        return restTemplate.getForObject("http://customer-service/customers/byRut/" + rut, Customer.class);
    }

    // --- M4 (TARIFAS) ---
    private Tariff getTariffByType(Long typeToolId) {
        // Llama al endpoint que creamos en el paso anterior en AmountsController
        return restTemplate.getForObject("http://amounts-service/amounts/tariff/" + typeToolId, Tariff.class);
    }

    // --- M1 (INVENTARIO) ---
    private Tool reserveToolInInventory(Long typeToolId) {
        return restTemplate.postForObject("http://inventory-service/tools/rent/" + typeToolId, null, Tool.class);
    }

    private void updateToolStatusInInventory(Long toolId, String newState) {
        restTemplate.put("http://inventory-service/tools/" + toolId + "/status?newStatus=" + newState, null);
    }

    private Tool getToolFromInventory(Long toolId) {
        // NECESITAMOS este endpoint en M1 para saber el tipo de herramienta y cobrar bien
        return restTemplate.getForObject("http://inventory-service/tools/" + toolId, Tool.class);
    }

    // --- M5 (KARDEX) ---
    private void registerKardexMovement(String type, Long loanId, Long userId, List<Long> toolIds) {
        Map<String, Object> req = new HashMap<>();
        req.put("typeMove", type);
        req.put("loanId", loanId);
        req.put("userId", userId);
        req.put("toolIds", toolIds);
        // Usamos String.class para ignorar el formato de respuesta y evitar errores de mapeo
        restTemplate.postForObject("http://kardexAndMovements-service/movements/registerMovement", req, String.class);
    }

    public List<LoanEntity> getActiveLoans() {
        return loanRepo.findByStateIsNot("RETURNED");
    }

    /**
     * RF2.4 Automatically calculate late fees (daily rate)
     */
    public int RentalAmount(List<Long> typeToolIds, LocalDate deliveryDate, LocalDate returnDate) {
        List<Long> reservedToolIds = new ArrayList<>();
        int totalRentalAmount = 0;

        // 4. Procesar herramientas: Calcular precio (M4) y Reservar Stock (M1)
        for (Long typeId : typeToolIds) {
            // Llamada a M4 (Tarifas)
            Tariff tariff = getTariffByType(typeId);

            // RF 2.4: Calcular monto arriendo (Tarifa * Días)
            long days = ChronoUnit.DAYS.between(deliveryDate, returnDate);
            if (days == 0) days = 1; // Mínimo 1 día

            totalRentalAmount += (tariff.getDailyRate() * days);

            // Llamada a M1 (Inventario)
            Tool reservedTool = reserveToolInInventory(typeId);
            reservedToolIds.add(reservedTool.getIdTool());
        }

        return totalRentalAmount;
    }

    // Asegúrate de importar LoanResponse y ArrayList

    public List<LoanResponse> getAllLoansWithDetails() {
        List<LoanEntity> loans = loanRepo.findAll();
        List<LoanResponse> responseList = new ArrayList<>();

        for (LoanEntity loan : loans) {
            // 1. Obtener Cliente (Aggregation)
            Customer customerObj = new Customer();
            try {
                // Buscamos el cliente por ID en el microservicio de clientes
                customerObj = restTemplate.getForObject(
                        "http://customer-service/customers/" + loan.getIdCustomer(),
                        Customer.class
                );
            } catch (Exception e) {
                // Si falla, ponemos datos dummy para que el frontend no se rompa
                customerObj.setId(loan.getIdCustomer());
                customerObj.setName("Desconocido (Error conexión)");
            }

            // 2. Obtener Herramientas (Opcional por ahora para no complicar)
            // Para que el frontend no falle con "loan.tool.map", enviamos una lista vacía o parcial
            List<Tool> toolList = new ArrayList<>();
            // (Aquí podrías hacer otro bucle para buscar los nombres de las herramientas en inventory-service)

            // 3. Construir respuesta
            LoanResponse dto = new LoanResponse();
            dto.setIdLoan(loan.getIdLoan());
            dto.setCustomer(customerObj); // <--- ESTO ARREGLA EL ERROR DEL FRONTEND
            dto.setTool(toolList);
            dto.setDeliveryDate(loan.getDeliveryDate());
            dto.setReturnDate(loan.getReturnDate());
            dto.setRentalAmount(loan.getRentalAmount());
            dto.setState(loan.getState());

            responseList.add(dto);
        }

        return responseList;
    }
}