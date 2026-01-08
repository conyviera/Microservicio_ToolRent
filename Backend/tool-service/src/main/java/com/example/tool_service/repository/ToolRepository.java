package com.example.tool_service.repository;

import com.example.tool_service.entity.ToolEntity;
import com.example.tool_service.entity.TypeToolEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ToolRepository extends JpaRepository<ToolEntity,Long> {

    @Override
    Optional<ToolEntity> findById(Long id);

    List<ToolEntity> findAll();

    Optional<ToolEntity> findFirstByIdtypeToolAndState (Long typeToolId, String state);

    List<ToolEntity> findAllByIdtypeTool(Long idtypeTool);
}
