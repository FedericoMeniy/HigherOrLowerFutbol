// Archivo: src/main/java/com/Meniy_Jordan_Lopez_Acuna_delValle/HigherLowerFutbol/controller/JugadorController.java

package com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.controller;

import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.dto.AuthenticationResponse;
import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.dto.LoginDTO;
import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.dto.RegistroDTO;
import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.service.JugadorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.core.AuthenticationException;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Permite peticiones desde el frontend
public class JugadorController {

    private final JugadorService jugadorService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> registrar(@RequestBody RegistroDTO request) {
        return ResponseEntity.ok(jugadorService.registrarJugador(request));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO request) {
        try{
            AuthenticationResponse response = jugadorService.autenticarJugador(request);
            return ResponseEntity.ok(response);
        }catch(BadCredentialsException e){
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Error de autenticacion", "message", e.getMessage()));
        }

    }

    // El endpoint de logout ya no es necesario en el backend para un sistema JWT simple.
    // El cliente (frontend) es responsable de borrar el token para "cerrar sesión".
}