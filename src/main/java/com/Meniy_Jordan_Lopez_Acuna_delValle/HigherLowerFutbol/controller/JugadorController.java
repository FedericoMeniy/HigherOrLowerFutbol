package com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.controller;

import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.dto.AuthenticationResponse;
import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.dto.LoginDTO;
import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.dto.RegistroDTO;
import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.service.JugadorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@Tag(name = "Autenticación de Jugadores", description = "Endpoints para el registro y login de jugadores.")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class JugadorController {

    private final JugadorService jugadorService;

    @Operation(summary = "Registrar un nuevo jugador",
            description = "Crea una nueva cuenta de jugador. Si el registro es exitoso, devuelve un token JWT para ser usado en las peticiones autenticadas.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Jugador registrado con éxito",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthenticationResponse.class))),
            @ApiResponse(responseCode = "400", description = "Error en la solicitud (ej: el email o username ya existen)",
                    content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"error\": \"El email ya se encuentra registrado.\"}")))
    })
    @PostMapping("/register")
    public ResponseEntity<?> registrar(
            @RequestBody(description = "Datos del nuevo jugador a registrar.", required = true,
                    content = @Content(schema = @Schema(implementation = RegistroDTO.class)))
            @org.springframework.web.bind.annotation.RequestBody RegistroDTO request) {
        try {
            AuthenticationResponse response = jugadorService.registrarJugador(request);
            return ResponseEntity.ok(response);
        } catch (IllegalStateException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(summary = "Autenticar un jugador (Login)",
            description = "Verifica las credenciales (email y contraseña) de un jugador. Si son correctas, devuelve un token JWT para futuras peticiones.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login exitoso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthenticationResponse.class))),
            @ApiResponse(responseCode = "401", description = "Credenciales incorrectas (email o contraseña inválidos)",
                    content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"error\": \"Error de autenticacion\", \"message\": \"La contraseña es incorrecta.\"}")))
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody(description = "Credenciales del jugador para iniciar sesión.", required = true,
                    content = @Content(schema = @Schema(implementation = LoginDTO.class)))
            @org.springframework.web.bind.annotation.RequestBody LoginDTO request) {
        try {
            AuthenticationResponse response = jugadorService.autenticarJugador(request);
            return ResponseEntity.ok(response);
        } catch (BadCredentialsException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Error de autenticacion", "message", e.getMessage()));
        }
    }
}