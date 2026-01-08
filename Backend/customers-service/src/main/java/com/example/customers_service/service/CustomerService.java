package com.example.customers_service.service;

import com.example.customers_service.entity.CustomerEntity;
import com.example.customers_service.repository.CustomerRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {

    private final CustomerRepository customerRepo;

    public CustomerService(CustomerRepository customerRepo) {
        this.customerRepo = customerRepo;
    }

    /**
     * RF3.1: Register a new customer validating that all attributes are correct
     * (Lógica recuperada de tu monolito)
     */
    public CustomerEntity registerCustomer(CustomerEntity customer) {
        // 1. Validaciones originales restauradas
        if (customer.getName() == null || customer.getName().trim().isEmpty()) {
            throw new IllegalStateException("El nombre es obligatorio.");
        }
        if (customer.getPhoneNumber() == null || customer.getPhoneNumber().trim().isEmpty()){
            throw new IllegalStateException("El numero de telefono es obligatorio.");
        }
        if (customer.getRut() == null || customer.getRut().trim().isEmpty()){
            throw new IllegalStateException("El rut es obligatorio.");
        }
        if (customer.getEmail() == null || customer.getEmail().trim().isEmpty()){
            throw new IllegalStateException("El correo es obligatorio.");
        }

        // 2. Validación de duplicados
        if(customerRepo.findByRut(customer.getRut()).isPresent()){
            throw new IllegalStateException("El rut ya existe");
        }

        // 3. Asignar estado inicial
        customer.setState("ACTIVE");

        return customerRepo.save(customer);
    }

    /**
     * Utility: Get Customer by RUT
     * (Usado por M2 para obtener datos del cliente)
     */
    public CustomerEntity getCustomerByRut(String rut) {
        return customerRepo.findByRut(rut)
                .orElseThrow(() -> new EntityNotFoundException("No existe el cliente con RUT " + rut));
    }

    /**
     * RF3.2: Change customer status (RESTRINGIDO/ACTIVO)
     * NOTA: En microservicios, este método es llamado por M2 (Préstamos)
     * cuando detecta una deuda, ya que M3 no tiene acceso a la tabla de préstamos.
     */
    @Transactional
    public void updateCustomerState(Long id, String newState) {
        CustomerEntity customer = customerRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado con ID: " + id));

        customer.setState(newState);
        customerRepo.save(customer);
    }

    //---------------------------------Utility---------------------------------------

    public List<CustomerEntity> getAllCustomers() {
        return customerRepo.findAll();
    }

    public Boolean getStateCustomer(Long id){
        CustomerEntity customer = customerRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No existe el cliente con ID " + id));
        return "ACTIVE".equalsIgnoreCase(customer.getState());
    }

    // En CustomerService.java
    public ResponseEntity<CustomerEntity> getCustomerById(Long id) {
        Optional<CustomerEntity> customer = customerRepo.findById(id);
        if (customer.isPresent()) {
            return ResponseEntity.ok(customer.get());
        }
        return ResponseEntity.notFound().build();
    }
}