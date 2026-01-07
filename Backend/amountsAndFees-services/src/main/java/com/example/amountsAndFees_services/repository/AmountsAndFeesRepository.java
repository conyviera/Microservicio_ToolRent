package com.example.amountsAndFees_services.repository;

import com.example.amountsAndFees_services.entity.AmountsAndFeesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AmountsAndFeesRepository extends JpaRepository<AmountsAndFeesEntity,Long> {

    Optional<AmountsAndFeesEntity> findByIdTypeTool(Long idTypeTool);
}
