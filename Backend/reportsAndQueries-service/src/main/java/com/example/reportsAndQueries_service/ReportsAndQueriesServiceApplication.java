package com.example.reportsAndQueries_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication
public class ReportsAndQueriesServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReportsAndQueriesServiceApplication.class, args);
	}

}
