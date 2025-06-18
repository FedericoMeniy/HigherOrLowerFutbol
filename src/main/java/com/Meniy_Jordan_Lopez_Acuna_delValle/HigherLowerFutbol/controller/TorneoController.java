package com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.controller;

import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.dto.TorneoDTO;
import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.entity.Torneo;
import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.service.TorneoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping()

public class TorneoController {

    @Autowired
    private TorneoService torneoService;

    /**
     * Endpoint para crear un nuevo torneo de amigos.
     * Recibe los datos del torneo en el cuerpo de la petición.
     */
    @PostMapping("/crear-amigos")
    public ResponseEntity<?> crearTorneoAmigos(@RequestBody TorneoDTO torneoDTO) {

        try {
            Torneo nuevoTorneo = torneoService.crearTorneoPrivado(torneoDTO);

            return ResponseEntity.ok(nuevoTorneo);

        } catch (RuntimeException e) {

            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
