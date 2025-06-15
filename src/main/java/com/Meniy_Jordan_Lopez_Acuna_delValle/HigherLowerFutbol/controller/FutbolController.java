package com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.controller;

import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.service.FutbolApiService;
import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.service.FutbolistaDataSyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequestMapping("/apii/futbol")
public class FutbolController {

    private FutbolApiService futbolApiService;
    private FutbolistaDataSyncService syncService;

    @Autowired
    public FutbolController(FutbolApiService futbolApiService, FutbolistaDataSyncService syncService) {
        this.futbolApiService = futbolApiService;
        this.syncService = syncService;
    }

    @PostMapping("/sync/liga/{ligaId}/temporada/{temporada}")
    public ResponseEntity<String> sincronizarLigaCompleta(@PathVariable int ligaId, @PathVariable int temporada) {
        new Thread(() -> syncService.sincronizarLigaEntera(ligaId, temporada)).start();
        return ResponseEntity.ok("Sincronización de la liga " + ligaId + " iniciada en segundo plano.");
    }

    @GetMapping("/jugador/id/{jugadorId}/temporada/{temporadaId}")
    public ResponseEntity<String>jugadorPorId(@PathVariable int jugadorId, @PathVariable int temporadaId){
        String json = futbolApiService.obtenerJugadorPorId(jugadorId, temporadaId);
        return ResponseEntity.ok(json);
    }


    /*
    @GetMapping("/{ligaId}/{temporada}")
    public ResponseEntity<String> traerEquipos(@PathVariable int ligaId, @PathVariable int temporada) {
        String json = futbolApiService.obtenerEquiposPorLiga(ligaId, temporada);
        return ResponseEntity.ok(json);
    }
     */

    @GetMapping("/{equipoId}")
    public ResponseEntity<String> traerJugadores(@PathVariable int equipoId){
        String json = futbolApiService.obtenerJugadorPorLiga(equipoId);
        return ResponseEntity.ok(json);
    }

    @PostMapping("/sync/equipo/{equipoId}/temporada/{temporada}")
    public ResponseEntity<String> sincronizarEquipo(
            @PathVariable int equipoId,
            @PathVariable int temporada) {
        try {
            // Ahora 'syncService' es reconocido y no dará error
            syncService.sincronizarJugadoresDeEquipo(equipoId, temporada);
            return ResponseEntity.ok("Sincronización del equipo " + equipoId + " iniciada correctamente.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error durante la sincronización: " + e.getMessage());
        }
    }

    /*
    @GetMapping("/jugador/{jugadorId}/temporada/{temporadaId}")
    public ResponseEntity<String>obtenerJugadorEstadi(@PathVariable int jugadorId, @PathVariable int temporadaId){
        String json = futbolApiService.obtenerEstadisticaJugador(jugadorId, temporadaId);
        return ResponseEntity.ok(json);
    }
     */


}
