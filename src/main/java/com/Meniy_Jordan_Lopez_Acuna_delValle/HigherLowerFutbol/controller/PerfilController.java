package com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.controller;

import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.dto.PerfilDTO;
import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.service.JugadorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/perfil")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PerfilController {

    private final JugadorService jugadorService;

    @GetMapping
    public ResponseEntity<PerfilDTO> getPerfil(Principal principal) {
        PerfilDTO perfil = jugadorService.obtenerPerfil(principal.getName());
        return ResponseEntity.ok(perfil);
    }
}