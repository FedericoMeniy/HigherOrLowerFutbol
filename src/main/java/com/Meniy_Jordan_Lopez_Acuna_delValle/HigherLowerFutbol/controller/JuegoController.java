package com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.controller;

import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.dto.PartidaTorneoDTO;
import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.dto.PuntajeDTO;
import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.dto.RondaResultado;
import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.entity.DetalleTorneo;
import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.service.JuegoService;
import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.service.JugadorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/juego")
@CrossOrigin(origins = "*") //
@RequiredArgsConstructor
public class JuegoController {

    private final JuegoService juegoService;
    private final JugadorService jugadorService;


    @GetMapping("/ronda")
    public ResponseEntity<RondaResultado> nuevaRonda() {
        try {
            RondaResultado resultadoRonda = juegoService.jugarRonda("mayor"); //
            return ResponseEntity.ok(resultadoRonda);
        } catch (IllegalStateException e) {
            System.err.println("Error al generar la ronda: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        } catch (Exception e) {
            System.err.println("Error inesperado en el controlador del juego: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    //ronda para jugar en el torneo
    @GetMapping("/{torneoId}/nueva-ronda")
    public ResponseEntity<RondaResultado> nuevaRondaDeTorneo(@PathVariable Long torneoId) {
        try {
            RondaResultado ronda = juegoService.generarRondaParaTorneo(); //
            return ResponseEntity.ok(ronda);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(null);
        }
    }

    @PostMapping("/registrar-partida-torneo")
    public ResponseEntity<?> registrarPartidaDeTorneo(@RequestBody PartidaTorneoDTO partidaDTO) {
        try {
            DetalleTorneo puntajeActualizado = juegoService.registrarResultadoPartida(partidaDTO); //
            return ResponseEntity.ok(puntajeActualizado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/guardar-puntaje")
    public ResponseEntity<?> guardarPuntaje(@RequestBody PuntajeDTO puntajeDTO, Principal principal) {
        try {
            int nuevoPuntaje = jugadorService.guardarPuntaje(principal.getName(), puntajeDTO);
            return ResponseEntity.ok(Map.of(
                    "mensaje", "Puntaje guardado exitosamente",
                    "nuevoPuntajeTotal", nuevoPuntaje
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}