package com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.controller;

import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.dto.TorneoDTO;
import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.dto.UnirseTorneoDTO;
import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.entity.DetalleTorneo;
import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.entity.Torneo;
import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.service.TorneoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/torneo")

public class TorneoController {

    @Autowired
    private TorneoService torneoService;

    /**
     * Endpoint para crear un nuevo torneo de amigos.
     * Recibe los datos del torneo en el cuerpo de la petición.
     */
    @PostMapping("/crear-amigos")
    public ResponseEntity<?> crearTorneoAmigos(@RequestBody TorneoDTO torneoDTO, Principal principal) {

        try {
            Torneo nuevoTorneo = torneoService.crearTorneoPrivado(torneoDTO,principal.getName());

            return ResponseEntity.ok(nuevoTorneo);

        } catch (RuntimeException e) {

            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{torneoId}/leaderboard")
    public ResponseEntity<List<DetalleTorneo>> getLeaderboard(@PathVariable Long torneoId) {
        try {
            List<DetalleTorneo> leaderboard = torneoService.getLeaderboard(torneoId);
            return ResponseEntity.ok(leaderboard);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
    @PostMapping("/{torneoId}/unirse")
    public ResponseEntity<?> unirseATorneo(@PathVariable Long torneoId, @RequestBody UnirseTorneoDTO dto) {
        try {
            // Llamamos al método del servicio que ahora sí es público.
            Torneo torneoActualizado = torneoService.unirseTorneo(torneoId, dto);
            return ResponseEntity.ok(torneoActualizado);
        } catch (RuntimeException e) {
            // Atrapa errores como "Torneo no encontrado", "Contraseña incorrecta", etc.
            // y los devuelve como una respuesta clara al frontend.
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
