package com.example.customers_service.controller;

import com.example.customers_service.entity.CustomerEntity;
import com.example.customers_service.repository.CustomerRepository;
import com.example.customers_service.service.CustomerService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerService customerService;
    private final CustomerRepository customerRepository;

    public CustomerController(CustomerService customerService, CustomerRepository customerRepository) {
        this.customerService = customerService;
        this.customerRepository = customerRepository;
    }

    // RF3.1: Registrar (Ahora maneja tus validaciones)
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody CustomerEntity customer) {
        try {
            CustomerEntity saved = customerService.registerCustomer(customer);
            return ResponseEntity.ok(saved);
        } catch (IllegalStateException e) {
            // Retorna el mensaje exacto: "El nombre es obligatorio", "El rut ya existe", etc.
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error interno: " + e.getMessage());
        }
    }

    // Consulta por RUT (Usado por M2)
    @GetMapping("/byRut/{rut}")
    public ResponseEntity<?> getByRut(@PathVariable String rut) {
        try {
            return ResponseEntity.ok(customerService.getCustomerByRut(rut));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // RF3.2: Bloqueo/Desbloqueo (Llamado por M2 - Préstamos)
    @PutMapping("/internal/state/{id}")
    public ResponseEntity<?> updateState(@PathVariable Long id, @RequestParam String state) {
        try {
            customerService.updateCustomerState(id, state);
            return ResponseEntity.ok().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<CustomerEntity>> getAll() {
        return ResponseEntity.ok(customerService.getAllCustomers());
    }

    @GetMapping("/{id}/state")
    public ResponseEntity<Boolean> getState(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(customerService.getStateCustomer(id));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Endpoint para comunicación interna con Loans-Service
    @GetMapping("/{id}")
    public ResponseEntity<CustomerEntity> getCustomerById(@PathVariable Long id) {
        return customerService.getCustomerById(id);
    }
}