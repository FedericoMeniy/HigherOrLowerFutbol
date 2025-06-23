package com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.controller;

import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.dto.*;
import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.entity.DetalleTorneo;
import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.entity.Torneo;
import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.exceptions.TorneoException;
import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.service.TorneoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/torneo")
@Tag(name = "Gestión de Torneos", description = "Endpoints para crear, listar, unirse y administrar torneos de jugadores.")
public class TorneoController {

    @Autowired
    private TorneoService torneoService;

    @Operation(summary = "Listar torneos disponibles", description = "Devuelve una lista de torneos filtrados por tipo (ADMIN o PRIVADO) y opcionalmente por nombre.")
    @ApiResponses(value = @ApiResponse(responseCode = "200", description = "Lista de torneos obtenida con éxito"))
    @GetMapping("/disponibles")
    public ResponseEntity<List<TorneoDisponibleDTO>> getTorneosDisponibles(
            @Parameter(description = "Tipo de torneo a filtrar: 'ADMIN' o 'PRIVADO'", required = true, example = "PRIVADO") @RequestParam String tipo,
            @Parameter(description = "Filtro opcional por nombre del torneo (no sensible a mayúsculas)") @RequestParam(required = false) String nombre) { // Se añadió este parámetro
        List<TorneoDisponibleDTO> torneos = torneoService.getTorneosDisponiblesPorTipo(tipo, nombre); // Se pasa el nuevo parámetro al servicio
        return ResponseEntity.ok(torneos);
    }

    @Operation(summary = "Crear un torneo privado (de amigos)", description = "Crea un nuevo torneo privado con contraseña. Requiere autenticación.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Torneo creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Error en la solicitud (ej: nombre de torneo ya existe)"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/crear-amigos")
    public ResponseEntity<?> crearTorneoAmigos(@RequestBody TorneoDTO torneoDTO, Principal principal) {
        try {
            Torneo nuevoTorneo = torneoService.crearTorneoPrivado(torneoDTO, principal.getName());
            return ResponseEntity.ok(nuevoTorneo);
        }catch (TorneoException e){

            return ResponseEntity.badRequest().body(e.getMessage());
        }
        catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @Operation(summary = "Ver la tabla de posiciones (leaderboard) de un torneo", description = "Obtiene la lista de participantes de un torneo, ordenada por puntaje descendente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Leaderboard obtenida con éxito"),
            @ApiResponse(responseCode = "400", description = "Error en la solicitud (ej: el torneo no existe)")
    })
    @GetMapping("/{torneoId}/leaderboard")
    public ResponseEntity<List<DetalleTorneo>> getLeaderboard(@PathVariable Long torneoId) {
        try {
            List<DetalleTorneo> leaderboard = torneoService.getLeaderboard(torneoId);
            return ResponseEntity.ok(leaderboard);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
    @Operation(summary = "Unirse a un torneo privado", description = "Permite a un jugador unirse a un torneo privado usando la contraseña. Requiere autenticación.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Unión al torneo exitosa"),
            @ApiResponse(responseCode = "400", description = "Error (ej: contraseña incorrecta, jugador ya inscrito)"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/{torneoId}/unirse")
    public ResponseEntity<?> unirseATorneo(@PathVariable Long torneoId,
                                           @RequestBody UnirseTorneoDTO dto,
                                           Principal principal) {
        try {
            Torneo torneoActualizado = torneoService.unirseTorneo(torneoId, dto, principal.getName());
            return ResponseEntity.ok(torneoActualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Finalizar un torneo y repartir premios (Solo Admins)", description = "Marca un torneo como finalizado y reparte los premios a los ganadores. Requiere rol de Administrador.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Torneo finalizado y premios repartidos"),
            @ApiResponse(responseCode = "400", description = "Error (ej: el torneo no es de tipo Admin)"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/{torneoId}/finalizar")
    public ResponseEntity<?> finalizarTorneo(@PathVariable Long torneoId) {
        try {
            torneoService.finalizarTorneoYRepartirPremios(torneoId);
            return ResponseEntity.ok("Torneo finalizado y premios repartidos exitosamente.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Crear un torneo oficial (Solo Admins)", description = "Crea un nuevo torneo oficial con costo y premio. Requiere rol de Administrador.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Torneo oficial creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Error en la solicitud"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado (no es Admin)")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/crear-oficial")
    public ResponseEntity<?> crearTorneoOficial(@RequestBody TorneoOficialDTO torneoDTO, Principal principal) {
        try {
            if (principal == null) {
                return ResponseEntity.status(401).body("No estás autenticado.");
            }

            Torneo nuevoTorneo = torneoService.crearTorneoOficial(torneoDTO, principal.getName());
            return ResponseEntity.ok(nuevoTorneo);

        }catch(TorneoException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Unirse a un torneo oficial", description = "Permite a un jugador unirse a un torneo oficial, descontando los puntos de entrada. Requiere autenticación.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Unión al torneo exitosa"),
            @ApiResponse(responseCode = "400", description = "Error (ej: no tienes suficientes puntos, torneo no activo)"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/{torneoId}/unirse-oficial")
    public ResponseEntity<?> unirseATorneoOficial(@PathVariable Long torneoId, Principal principal) {
        try {
            if (principal == null) {
                return ResponseEntity.status(401).body("Debes iniciar sesión para unirte a un torneo.");
            }

            Torneo torneoActualizado = torneoService.unirseATorneoOficial(torneoId, principal.getName());
            return ResponseEntity.ok(torneoActualizado);

        } catch (RuntimeException e) {

            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Actualizar un torneo oficial (Solo Admins)", description = "Permite a un administrador modificar el premio y/o costo de un torneo. Requiere rol de Administrador.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Torneo actualizado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Error en la solicitud"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado (no es Admin)")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/{torneoId}/actualizar-oficial")
    public ResponseEntity<?> actualizarTorneoOficial(@PathVariable Long torneoId, @RequestBody ActualizarTorneoOficialDTO dto, Principal principal) {

        try {
            if (principal == null) {
                return ResponseEntity.status(401).body("No estás autenticado.");
            }

            Torneo torneoActualizado = torneoService.actualizarTorneoOficial(torneoId, dto, principal.getName());
            return ResponseEntity.ok(torneoActualizado);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Eliminar un torneo (Solo Admins)", description = "Elimina un torneo de forma permanente. Requiere rol de Administrador.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Torneo eliminado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Error (ej: el torneo no fue encontrado)"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado (no es Admin)")
    })
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{torneoId}/eliminar")
    public ResponseEntity<?> eliminarTorneo(@PathVariable Long torneoId, Principal principal) {
        try {

            if (principal == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No estás autenticado.");
            }

            torneoService.eliminarTorneo(torneoId, principal.getName());

            return ResponseEntity.ok("Torneo eliminado exitosamente.");

        } catch (SecurityException e) {

            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());

        } catch (RuntimeException e) {

            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Obtener los torneos en los que un jugador está inscrito", description = "Requiere autenticación.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de torneos inscritos obtenida con éxito"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/inscriptos")
    public ResponseEntity<List<TorneoDisponibleDTO>> getTorneosInscritos(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<TorneoDisponibleDTO> torneos = torneoService.getTorneosInscritos(principal.getName());
        return ResponseEntity.ok(torneos);
    }

    @Operation(summary = "Obtener los torneos creados por un jugador", description = "Requiere autenticación.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de torneos creados obtenida con éxito"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/creados")
    public ResponseEntity<List<TorneoDisponibleDTO>> getTorneosCreados(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<TorneoDisponibleDTO> torneos = torneoService.getTorneosCreados(principal.getName());
        return ResponseEntity.ok(torneos);
    }

}