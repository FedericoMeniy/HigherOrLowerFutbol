package com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.controller;

import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.dto.PartidaTorneoDTO;
import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.dto.PuntajeDTO;
import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.dto.RondaResultado;
import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.entity.DetalleTorneo;
import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.service.JuegoService;
import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.service.JugadorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/juego")
@Tag(name = "Mecánicas del Juego", description = "Endpoints para las lógicas principales del juego, como obtener rondas y registrar resultados.")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class JuegoController {

    private final JuegoService juegoService;
    private final JugadorService jugadorService;

    @Operation(summary = "Obtener una nueva ronda del modo clásico",
            description = "Devuelve dos futbolistas y una pregunta aleatoria para el modo de juego principal. Este endpoint es público y no requiere autenticación.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ronda generada con éxito",
                    content = @Content(schema = @Schema(implementation = RondaResultado.class))),
            @ApiResponse(responseCode = "503", description = "Servicio no disponible (ej: no hay suficientes futbolistas en la BD)",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Error inesperado en el servidor",
                    content = @Content)
    })
    @GetMapping("/ronda")
    public ResponseEntity<RondaResultado> nuevaRonda() {
        try {
            RondaResultado resultadoRonda = juegoService.jugarRonda("mayor");
            return ResponseEntity.ok(resultadoRonda);
        } catch (IllegalStateException e) {
            System.err.println("Error al generar la ronda: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        } catch (Exception e) {
            System.err.println("Error inesperado en el controlador del juego: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Obtener una nueva ronda para un torneo",
            description = "Genera una nueva ronda de juego específicamente para un torneo. Requiere autenticación.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ronda de torneo generada con éxito",
                    content = @Content(schema = @Schema(implementation = RondaResultado.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content),
            @ApiResponse(responseCode = "503", description = "Servicio no disponible", content = @Content)
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/{torneoId}/nueva-ronda")
    public ResponseEntity<RondaResultado> nuevaRondaDeTorneo(
            @Parameter(description = "ID del torneo para el que se genera la ronda", required = true)
            @PathVariable Long torneoId) {
        try {
            RondaResultado ronda = juegoService.generarRondaParaTorneo();
            return ResponseEntity.ok(ronda);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(null);
        }
    }

    @Operation(summary = "Registrar el resultado de una partida de torneo",
            description = "Registra los puntos obtenidos por un jugador en una partida de un torneo específico. Requiere autenticación.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Partida registrada y puntaje actualizado",
                    content = @Content(schema = @Schema(implementation = DetalleTorneo.class))),
            @ApiResponse(responseCode = "400", description = "Error en la solicitud (ej: el jugador ya jugó todas sus partidas, o no está inscrito)",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content)
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/registrar-partida-torneo")
    public ResponseEntity<?> registrarPartidaDeTorneo(
            @RequestBody(description = "Datos de la partida, incluyendo IDs del torneo/jugador y los puntos obtenidos", required = true)
            @org.springframework.web.bind.annotation.RequestBody PartidaTorneoDTO partidaDTO) {
        try {
            DetalleTorneo puntajeActualizado = juegoService.registrarResultadoPartida(partidaDTO);
            return ResponseEntity.ok(puntajeActualizado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Guardar puntaje del modo clásico",
            description = "Añade los puntos ganados en una partida del modo clásico al puntaje total del jugador autenticado. Requiere autenticación.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Puntaje guardado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Error al procesar la solicitud"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/guardar-puntaje")
    public ResponseEntity<?> guardarPuntaje(
            @RequestBody(description = "DTO con la cantidad de puntos a sumar", required = true)
            @org.springframework.web.bind.annotation.RequestBody PuntajeDTO puntajeDTO,
            Principal principal) {
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