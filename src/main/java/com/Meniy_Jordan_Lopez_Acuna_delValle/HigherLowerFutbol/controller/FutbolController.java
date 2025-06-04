package com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.controller;

import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.service.FutbolApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/apii/futbol")
public class FutbolController {

    private FutbolApiService futbolApiService;

    public FutbolController(FutbolApiService futbolApiService) {
        this.futbolApiService = futbolApiService;
    }

    @GetMapping("/jugador/id/{jugadorId}/temporada/{temporadaId}")
    public ResponseEntity<String>jugadorPorId(@PathVariable int jugadorId, @PathVariable int temporadaId){
        String json = futbolApiService.obtenerJugadorPorId(jugadorId, temporadaId);
        return ResponseEntity.ok(json);
    }

    @GetMapping("/{ligaId}/{temporada}")
    public ResponseEntity<String> traerEquipos(@PathVariable int ligaId, @PathVariable int temporada) {
        String json = futbolApiService.obtenerEquiposPorLiga(ligaId, temporada);
        return ResponseEntity.ok(json);
    }

    @GetMapping("/{equipoId}")
    public ResponseEntity<String> traerJugadores(@PathVariable int equipoId){
        String json = futbolApiService.obtenerJugadorPorLiga(equipoId);
        return ResponseEntity.ok(json);
    }

    @GetMapping("/jugador/{jugadorId}/temporada/{temporadaId}")
    public ResponseEntity<String>obtenerJugadorEstadi(@PathVariable int jugadorId, @PathVariable int temporadaId){
        String json = futbolApiService.obtenerEstadisticaJugador(jugadorId, temporadaId);
        return ResponseEntity.ok(json);
    }
}
