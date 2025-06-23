package com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.service;


import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.entity.Torneo;
import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.entity.TorneoAdmin;
import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.entity.TorneoPrivado;
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
    @Autowired
    private TorneoService torneoService;

    @Scheduled(fixedRate = 60000)
    public void activarTorneosPendientes() {
        System.out.println("Buscando torneos para activar...");

        List<Torneo> torneosParaActivar = torneoRepository.findByEstadoTorneoAndFechaCreacionBefore(EstadoTorneo.PENDIENTE, LocalDateTime.now());

        for (Torneo torneo : torneosParaActivar) {
            torneo.setEstadoTorneo(EstadoTorneo.ACTIVO);
            torneoRepository.save(torneo);
            System.out.println("¡Torneo activado!: " + torneo.getNombre());
        }
    }
    @Scheduled(fixedRate = 60000)
    public void finalizarTorneosActivos() {
        System.out.println("Buscando torneos para finalizar...");

        List<Torneo> torneosParaFinalizar = torneoRepository
                .findByEstadoTorneoAndFechaFinBefore(EstadoTorneo.ACTIVO, LocalDateTime.now());

        for (Torneo torneo : torneosParaFinalizar) {

            System.out.println("Finalizando torneo: " + torneo.getNombre() + " (ID: " + torneo.getId() + ")");

            if (torneo instanceof TorneoAdmin) {

                torneoService.finalizarTorneoYRepartirPremios(torneo.getId());

            } else if (torneo instanceof TorneoPrivado) {

                torneo.setEstadoTorneo(EstadoTorneo.FINALIZADO);
                torneoRepository.save(torneo);
            }
        }
    }
}
