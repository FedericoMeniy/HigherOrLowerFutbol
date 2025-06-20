package com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.controller;

import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.dto.PartidaTorneoDTO;
import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.dto.RondaResultado;
import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.entity.DetalleTorneo;
import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.service.JuegoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/juego")
@CrossOrigin(origins = "*") // Permite peticiones desde cualquier origen
public class JuegoController {

    @Autowired
    private JuegoService juegoService; // Se inyecta el servicio con la lógica del juego

    /**
     * Endpoint para solicitar una nueva ronda del juego.
     * El frontend llamará a este endpoint para obtener los datos de los dos futbolistas
     * y la pregunta a mostrar.
     *
     * @return Un objeto ResponseEntity que contiene los datos de la ronda o un error.
     */
    @GetMapping("/ronda")
    public ResponseEntity<RondaResultado> nuevaRonda() {
        try {
            // Llamamos al servicio para que prepare una nueva ronda.
            // La elección del usuario no importa en este punto, solo queremos generar los datos.
            // Pasamos un valor por defecto como "mayor".
            RondaResultado resultadoRonda = juegoService.jugarRonda("mayor");
            return ResponseEntity.ok(resultadoRonda);
        } catch (IllegalStateException e) {
            // Esto se ejecuta si el servicio lanza la excepción (ej: no hay suficientes jugadores)
            System.err.println("Error al generar la ronda: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        } catch (Exception e) {
            System.err.println("Error inesperado en el controlador del juego: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{torneoId}/nueva-ronda")
    public ResponseEntity<RondaResultado> nuevaRondaDeTorneo(@PathVariable Long torneoId) {
        try {
            // Podríamos añadir lógica para verificar si el torneoId es válido.
            RondaResultado ronda = juegoService.generarRondaParaTorneo();
            return ResponseEntity.ok(ronda);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(null);
        }
    }

    @PostMapping("/registrar-partida-torneo")
    public ResponseEntity<?> registrarPartidaDeTorneo(@RequestBody PartidaTorneoDTO partidaDTO) {
        try {
            DetalleTorneo puntajeActualizado = juegoService.registrarResultadoPartida(partidaDTO);
            return ResponseEntity.ok(puntajeActualizado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}