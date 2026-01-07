package com.example.kardexAndMovements_service.repository;

import com.example.kardexAndMovements_service.entity.KardexEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface KardexRepository extends JpaRepository<KardexEntity,Long> {
    @Override
    Optional<KardexEntity> findById(Long kardexId);

    List<KardexEntity> findAll();

    List<KardexEntity> findByDateBetween(LocalDateTime start, LocalDateTime end);



}
