package com.example.reportsAndQueries_service.service;

import com.example.reportsAndQueries_service.model.KardexItem;
import com.example.reportsAndQueries_service.model.LoanReport;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportService {

    private final RestTemplate restTemplate;

    public ReportService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * RF 6.1: Listar préstamos activos
     * Consulta a M2 (Loans)
     */
    public List<LoanReport> getActiveLoans() {
        // Llamada directa y simple.
        // Spring mapeará automáticamente el "idCustomer" del JSON al "idCustomer" de tu clase LoanReport
        return restTemplate.exchange(
                "http://loans-service/loans/active",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<LoanReport>>() {}
        ).getBody();
    }

    /**
     * RF 6.2: Listar clientes con atrasos
     * Estrategia: Obtener préstamos activos vencidos desde M2
     * (Opcional: llamar a Customers M3 para obtener detalles del cliente si solo tenemos ID)
     */
    public List<LoanReport> getClientsWithOverdueLoans() {
        List<LoanReport> activeLoans = getActiveLoans();
        if (activeLoans == null) return Collections.emptyList();

        // Filtramos los que su fecha de retorno sea anterior a hoy
        return activeLoans.stream()
                .filter(loan -> loan.getReturnDate().isBefore(java.time.LocalDate.now()))
                .collect(Collectors.toList());
    }

    /**
     * RF 6.3: Ranking de herramientas más prestadas
     * Consulta a M5 (Kardex) para obtener historial de movimientos tipo "PRESTAMO"
     */
    public Map<Long, Integer> getToolRanking() {
        // 1. Obtener todos los movimientos desde Kardex (Necesitamos un endpoint en M5 que devuelva todo o por rango amplio)
        // Por simplicidad, asumiremos que pedimos un rango de fechas grande o creamos un endpoint "all" en M5.
        // Usaremos el endpoint de rango que creamos en M5: /movements/range

        String url = "http://kardexAndMovements-service/movements/range?start=2024-01-01T00:00:00&end=2030-12-31T23:59:59";


        List<KardexItem> movements = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<KardexItem>>() {}
        ).getBody();

        if (movements == null) return Collections.emptyMap();

        // 2. Filtrar solo "PRESTAMO" y contar herramientas
        Map<Long, Integer> ranking = new HashMap<>();

        movements.stream()
                .filter(m -> "PRESTAMO".equalsIgnoreCase(m.getTypeMove()))
                .forEach(m -> {
                    for (Long toolId : m.getToolIds()) {
                        ranking.put(toolId, ranking.getOrDefault(toolId, 0) + 1);
                    }
                });

        // Retornamos el mapa (ID Herramienta -> Cantidad de veces prestada)
        // Podrías ordenarlo aquí o en el frontend
        return ranking;
    }
}