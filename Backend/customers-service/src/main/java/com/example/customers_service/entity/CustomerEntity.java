package com.example.customers_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCustomer;

    private String name;

    private String phoneNumber;

    private String rut;

    private String email;
    /**
     * State:
     * ACTIVE
     * RESTRICTED
     */
    private String state;

    @PrePersist
    public void prePersist() {
        if (this.state == null) {
            this.state = "ACTIVE";
        }
    }


}