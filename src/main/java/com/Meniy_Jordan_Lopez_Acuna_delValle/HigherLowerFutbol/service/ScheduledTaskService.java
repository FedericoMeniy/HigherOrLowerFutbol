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
    @Scheduled(fixedRate = 60000)
    public void finalizarTorneosActivos() {
        System.out.println("Buscando torneos para finalizar...");

        // 1. Usamos el nuevo método del repositorio para encontrar los torneos que ya terminaron.
        List<Torneo> torneosParaFinalizar = torneoRepository
                .findByEstadoTorneoAndFechaFinBefore(EstadoTorneo.ACTIVO, LocalDateTime.now());

        // 2. Recorremos la lista de torneos que deben ser finalizados.
        for (Torneo torneo : torneosParaFinalizar) {

            System.out.println("Finalizando torneo: " + torneo.getNombre() + " (ID: " + torneo.getId() + ")");

            // 3. ¡LÓGICA CONDICIONAL! Verificamos de qué tipo es el torneo.
            if (torneo instanceof TorneoAdmin) {
                // Si es un torneo de Admin, llamamos al método que ya tienes,
                // que se encarga de repartir los premios Y de cambiar el estado a FINALIZADO.
                torneoService.finalizarTorneoYRepartirPremios(torneo.getId());

            } else if (torneo instanceof TorneoPrivado) {
                // Si es un torneo privado, no hay premios que repartir.
                // Simplemente cambiamos su estado y lo guardamos.
                torneo.setEstadoTorneo(EstadoTorneo.FINALIZADO);
                torneoRepository.save(torneo);
            }
        }
    }


}
