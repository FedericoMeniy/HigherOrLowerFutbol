package com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.controller;

import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.dto.PerfilDTO;
import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.service.JugadorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/perfil")
@Tag(name = "Gestión de Perfil de Jugador", description = "Endpoint para obtener la información del perfil del jugador autenticado.")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PerfilController {

    private final JugadorService jugadorService;

    @Operation(summary = "Obtener el perfil del jugador autenticado",
            description = "Devuelve los datos del perfil (username, email y puntos totales) del jugador que realiza la petición. Requiere autenticación (enviar token JWT).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Perfil del jugador obtenido con éxito",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = PerfilDTO.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado. El token no fue proporcionado o es inválido.",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado en la base de datos",
                    content = @Content)
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping
    public ResponseEntity<PerfilDTO> getPerfil(Principal principal) {
        PerfilDTO perfil = jugadorService.obtenerPerfil(principal.getName());
        return ResponseEntity.ok(perfil);
    }
}