package com.example.kardexAndMovements_service.repository;

import com.example.kardexAndMovements_service.entity.KardexDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KardexDetailRepository extends JpaRepository<KardexDetailEntity, Long> {

    List<KardexDetailEntity> findByToolId(Long idTool);


}