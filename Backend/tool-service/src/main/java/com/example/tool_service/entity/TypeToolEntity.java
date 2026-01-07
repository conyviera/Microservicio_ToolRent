package com.example.tool_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TypeToolEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTypeTool;

    private String name;

    @Column(nullable = false)
    private String category;

}
