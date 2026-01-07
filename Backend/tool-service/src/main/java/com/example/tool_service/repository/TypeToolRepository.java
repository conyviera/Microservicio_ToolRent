package com.example.tool_service.repository;

import com.example.tool_service.entity.TypeToolEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TypeToolRepository extends JpaRepository<TypeToolEntity,Long>{

    Optional<TypeToolEntity> findByNameAndCategory(String name, String category);

    List <TypeToolEntity> findAll();

    @Query("select distinct t.category from TypeToolEntity t")
    List<String> findDistinctCategory();

}