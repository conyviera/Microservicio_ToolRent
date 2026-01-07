package com.example.loansAndRefunds_service.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Customer {

    // CAMBIO AQUÍ: Debe decir "idCustomer" porque así viene en el JSON
    @JsonProperty("idCustomer")
    private Long id;

    private String rut;
    private String name;
    private String phoneNumber;
    private String email;
    private String state;
}