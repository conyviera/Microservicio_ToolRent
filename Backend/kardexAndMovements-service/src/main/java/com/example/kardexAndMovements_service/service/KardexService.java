package com.example.kardexAndMovements_service.service;

import com.example.kardexAndMovements_service.entity.KardexDetailEntity;
import com.example.kardexAndMovements_service.entity.KardexEntity;
import com.example.kardexAndMovements_service.repository.KardexDetailRepository;
import com.example.kardexAndMovements_service.repository.KardexRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class KardexService {

    private final KardexRepository kardexRepo;
    private final KardexDetailRepository kardexDetailRepo;

    public KardexService(KardexRepository kardexRepo, KardexDetailRepository kardexDetailRepo) {
        this.kardexRepo = kardexRepo;
        this.kardexDetailRepo = kardexDetailRepo;
    }

    /**
     * register movement
     * Agregamos userId como parámetro para recibirlo correctamente
     */
    @Transactional
    public KardexEntity registerMovement(String typeMove, Long idLoan, Long userId, List<Long> idTools) {

        KardexEntity kardex = KardexEntity.builder()
                .typeMove(typeMove)
                .date(LocalDateTime.now())
                .quantity(idTools.size())
                .loanId(idLoan)
                .userId(userId) // <--- USAMOS EL DATO REAL
                .build();

        // Guardamos primero el padre para tener ID
        KardexEntity savedKardex = kardexRepo.save(kardex);

        for (Long toolId : idTools) {
            KardexDetailEntity detail = KardexDetailEntity.builder()
                    .toolId(toolId)
                    .kardex(savedKardex) // Asociamos al padre guardado
                    .build();
            // Nota: Si tienes CascadeType.ALL en la entidad, basta con agregarlo a la lista y guardar el padre al final.
            // Si no, guarda el detalle aquí. Asumiré Cascade por JPA standard.
            savedKardex.addDetail(detail);
        }

        return kardexRepo.save(savedKardex);
    }


    public List<KardexEntity> getAllMovementsOfATool(Long toolId){
        List<KardexDetailEntity> details = kardexDetailRepo.findByToolId(toolId);
        return details.stream()
                .map(KardexDetailEntity::getKardex)
                .distinct()
                .collect(Collectors.toList());
    }

    public List<KardexEntity> listByDateRange(LocalDateTime start, LocalDateTime end){
        return kardexRepo.findByDateBetween(start, end);
    }

    /**
     * Utility: Submit all transactions
     * @return
     */
    public List<KardexEntity> findAllKardex() {
        List<KardexEntity> kardexList = kardexRepo.findAll();
        return kardexList;
    }

}