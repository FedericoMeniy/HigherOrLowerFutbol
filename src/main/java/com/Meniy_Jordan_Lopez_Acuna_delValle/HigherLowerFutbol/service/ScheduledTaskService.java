package com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.service;


import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.entity.Torneo;
import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.enums.EstadoTorneo;
import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.repository.TorneoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ScheduledTaskService {

    @Autowired
    private TorneoRepository torneoRepository;

    /**
     * Este método se ejecutará automáticamente cada 60 segundos (60000 milisegundos).
     */
    @Scheduled(fixedRate = 60000)
    public void activarTorneosPendientes() {
        System.out.println("Buscando torneos para activar...");

        // 1. Buscamos todos los torneos que estén PENDIENTES y cuya fecha de inicio ya haya pasado.
        List<Torneo> torneosParaActivar = torneoRepository.findByEstadoTorneoAndFechaCreacionBefore(EstadoTorneo.PENDIENTE, LocalDateTime.now());

        // 2. Si encontramos alguno, lo activamos.
        for (Torneo torneo : torneosParaActivar) {
            torneo.setEstadoTorneo(EstadoTorneo.ACTIVO);
            torneoRepository.save(torneo);
            System.out.println("¡Torneo activado!: " + torneo.getNombre());
        }
    }

}
