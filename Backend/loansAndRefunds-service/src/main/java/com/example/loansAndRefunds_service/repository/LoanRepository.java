package com.example.loansAndRefunds_service.repository;

import com.example.loansAndRefunds_service.entity.LoanEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LoanRepository extends JpaRepository<LoanEntity, Long> {

    @Override
    Optional<LoanEntity> findById(Long id);

    List<LoanEntity> findByStateIsNot(String state);

    List<LoanEntity> findAll();

    // Este ya estaba correcto, lo mantenemos igual
    int countByIdCustomerAndState(Long idCustomer, String state);

    List<LoanEntity> findByState(String state);
}