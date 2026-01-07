package com.example.loansAndRefunds_service.repository;

import com.example.loansAndRefunds_service.entity.DebtsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DebtsRepository extends JpaRepository<DebtsEntity, Long> {

    // CORREGIDO: 'IdCustomer' en lugar de 'CustomerId'
    boolean existsByIdCustomerAndStatus(Long idCustomer, String status);

    List<DebtsEntity> findByLoan_IdLoan(Long idLoan);
}