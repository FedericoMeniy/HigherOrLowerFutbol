package com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.controller;

import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.service.FutbolApiService;
import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.service.FutbolistaDataSyncService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequestMapping("/apii/futbol")
@Tag(name = "API de Futbolistas (Externa)", description = "Endpoints para interactuar directamente con la API de api-sports.io")
public class FutbolController {

    private FutbolApiService futbolApiService;
    private FutbolistaDataSyncService syncService;

    @Autowired
    public FutbolController(FutbolApiService futbolApiService, FutbolistaDataSyncService syncService) {
        this.futbolApiService = futbolApiService;
        this.syncService = syncService;
    }

    public FutbolApiService getFutbolApiService() {
        return futbolApiService;
    }

    public void setFutbolApiService(FutbolApiService futbolApiService) {
        this.futbolApiService = futbolApiService;
    }

    public FutbolistaDataSyncService getSyncService() {
        return syncService;
    }

    public void setSyncService(FutbolistaDataSyncService syncService) {
        this.syncService = syncService;
    }


    @Operation(summary = "Obtener datos de un jugador por ID y temporada",
            description = "Consulta la API externa para obtener la información y estadísticas de un jugador específico para una temporada determinada.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Datos del jugador en formato JSON obtenidos con éxito"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor o al comunicarse con la API externa")
    })
    @GetMapping("/jugador/id/{jugadorId}/temporada/{temporadaId}")
    public ResponseEntity<String>jugadorPorId(@Parameter(description = "ID del jugador en la API de api-sports.io", required = true, example = "276")
                                                  @PathVariable int jugadorId,
                                              @Parameter(description = "Año de la temporada a consultar", required = true, example = "2021")
                                                  @PathVariable int temporadaId){
        String json = futbolApiService.obtenerJugadorPorId(jugadorId, temporadaId);
        return ResponseEntity.ok(json);
    }

    @Operation(summary = "Obtener la plantilla de un equipo por ID",
            description = "Consulta la API externa para obtener la lista de todos los jugadores que pertenecen a un equipo específico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Plantilla del equipo en formato JSON obtenida con éxito"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor o al comunicarse con la API externa")
    })
    @GetMapping("/{equipoId}")
    public ResponseEntity<String> traerJugadores(@Parameter(description = "ID del equipo en la API de api-sports.io", required = true, example = "40")
                                                     @PathVariable int equipoId){
        String json = futbolApiService.obtenerJugadorPorLiga(equipoId);
        return ResponseEntity.ok(json);
    }
}
