package com.example.amountsAndFees_services.service;

import com.example.amountsAndFees_services.entity.AmountsAndFeesEntity;
import com.example.amountsAndFees_services.repository.AmountsAndFeesRepository;
import jakarta.persistence.EntityNotFoundException; // Importante para errores 404
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AmountsAndFeesServices {

    private final AmountsAndFeesRepository amountRepo;

    public AmountsAndFeesServices(AmountsAndFeesRepository amountRepo) {
        this.amountRepo = amountRepo;
    }

    /**
     * Register or Update amount and fees
     */
    public AmountsAndFeesEntity registerAmountAndFees(Long idTypeTool, int replacementValue, int dailyRate, int debtRate) {

        if (idTypeTool == null) throw new IllegalStateException("El identificador no puede ser nulo");

        validatePositiveValues(replacementValue, dailyRate, debtRate);

        Optional<AmountsAndFeesEntity> amountExisting = amountRepo.findByIdTypeTool(idTypeTool);

        if (amountExisting.isPresent()) {
            AmountsAndFeesEntity amount = amountExisting.get();
            amount.setReplacementValue(replacementValue);
            amount.setDailyRate(dailyRate);
            amount.setDebtRate(debtRate);
            return amountRepo.save(amount);
        } else {
            AmountsAndFeesEntity newAmount = AmountsAndFeesEntity.builder()
                    .idTypeTool(idTypeTool)
                    .replacementValue(replacementValue)
                    .dailyRate(dailyRate)
                    .debtRate(debtRate)
                    .build();
            return amountRepo.save(newAmount);
        }
    }

    /**
     * RF4.1 Set daily rental rate
     */
    public AmountsAndFeesEntity setDailyRentalRate(Long idTypeTool, int dailyRate) {
        if (dailyRate < 0) throw new IllegalStateException("La tarifa diaria no puede ser negativa");

        AmountsAndFeesEntity amount = findOrThrow(idTypeTool); // Método helper para no repetir código

        amount.setDailyRate(dailyRate);
        return amountRepo.save(amount);
    }

    /**
     * RF4.2 Set daily late fee rate
     */
    public AmountsAndFeesEntity setDebtRentalRate(Long idTypeTool, int debtRate) {
        if (debtRate < 0) throw new IllegalStateException("La tarifa por deuda no puede ser negativa");

        AmountsAndFeesEntity amount = findOrThrow(idTypeTool);

        amount.setDebtRate(debtRate);
        return amountRepo.save(amount);
    }

    /**
     * RF4.3 Register the replacement cost of each tool
     */
    public AmountsAndFeesEntity registerReplacementValue(Long idTypeTool, int replacementValue) {
        if (replacementValue < 0) throw new IllegalStateException("El valor de reposición no puede ser negativo");

        AmountsAndFeesEntity amount = findOrThrow(idTypeTool);

        amount.setReplacementValue(replacementValue);
        return amountRepo.save(amount);
    }

    // ---------------- Helper ----------------

    /**
     * Helper 1: Search for the entity or throw an error if it does not exist
     */
    private AmountsAndFeesEntity findOrThrow(Long idTypeTool) {
        if (idTypeTool == null) throw new IllegalStateException("El identificador no puede ser nulo");

        return amountRepo.findByIdTypeTool(idTypeTool)
                .orElseThrow(() -> new EntityNotFoundException("No existen tarifas registradas para el tipo de herramienta: " + idTypeTool));
    }

    /**
     * Helper 2: Validate that the numbers are not negative
     */
    private void validatePositiveValues(int... values) {
        for (int value : values) {
            if (value < 0) {
                throw new IllegalStateException("Los valores monetarios no pueden ser negativos");
            }
        }
    }

    // --- INTEGRACIÓN CON M2 (Préstamos) ---

    /**
     * Llamado por LoanService para obtener el dailyRate
     */
    public AmountsAndFeesEntity getAmountByType(Long idTypeTool) {
        return findOrThrow(idTypeTool);
    }
}