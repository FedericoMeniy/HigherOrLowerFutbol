package com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.controller;

import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.dto.*;
import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.entity.DetalleTorneo;
import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.entity.Torneo;
import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.exceptions.TorneoException;
import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.service.TorneoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/torneo") //
public class TorneoController {

    @Autowired
    private TorneoService torneoService;

    // Endpoint para obtener torneos disponibles
    @GetMapping("/disponibles")
    public ResponseEntity<List<TorneoDisponibleDTO>> getTorneosDisponibles(
            @RequestParam String tipo,
            @RequestParam(required = false) String nombre) { // Se añadió este parámetro
        List<TorneoDisponibleDTO> torneos = torneoService.getTorneosDisponiblesPorTipo(tipo, nombre); // Se pasa el nuevo parámetro al servicio
        return ResponseEntity.ok(torneos);
    }
    // El resto de los endpoints ahora también estarán bajo /api/torneos
    @PostMapping("/crear-amigos")
    public ResponseEntity<?> crearTorneoAmigos(@RequestBody TorneoDTO torneoDTO, Principal principal) {
        try {
            Torneo nuevoTorneo = torneoService.crearTorneoPrivado(torneoDTO, principal.getName());
            return ResponseEntity.ok(nuevoTorneo);
        }catch (TorneoException e){

            return ResponseEntity.badRequest().body("Error en los datos del torneo: " + e.getMessage());
        }
        catch (RuntimeException e) {
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
    public ResponseEntity<?> unirseATorneo(@PathVariable Long torneoId,
                                           @RequestBody UnirseTorneoDTO dto,
                                           Principal principal) {
        try {
            // Pasar el email del usuario autenticado al servicio
            Torneo torneoActualizado = torneoService.unirseTorneo(torneoId, dto, principal.getName());
            return ResponseEntity.ok(torneoActualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{torneoId}/finalizar")
    public ResponseEntity<?> finalizarTorneo(@PathVariable Long torneoId) {
        try {
            torneoService.finalizarTorneoYRepartirPremios(torneoId);
            return ResponseEntity.ok("Torneo finalizado y premios repartidos exitosamente.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/crear-oficial")
    public ResponseEntity<?> crearTorneoOficial(@RequestBody TorneoOficialDTO torneoDTO, Principal principal) {
        try {
            if (principal == null) {
                return ResponseEntity.status(401).body("No estás autenticado.");
            }

            Torneo nuevoTorneo = torneoService.crearTorneoOficial(torneoDTO, principal.getName());
            return ResponseEntity.ok(nuevoTorneo);

        } catch (SecurityException e) {
            // Atrapamos específicamente el error de seguridad si un no-admin intenta crear el torneo
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            // Atrapamos otros errores generales
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{torneoId}/unirse-oficial")
    public ResponseEntity<?> unirseATorneoOficial(@PathVariable Long torneoId, Principal principal) {
        try {
            if (principal == null) {
                return ResponseEntity.status(401).body("Debes iniciar sesión para unirte a un torneo.");
            }

            // La lógica no cambia, principal.getName() sigue devolviendo el email.
            // Solo actualizamos la llamada para que coincida con la firma del método del servicio.
            Torneo torneoActualizado = torneoService.unirseATorneoOficial(torneoId, principal.getName());
            return ResponseEntity.ok(torneoActualizado);

        } catch (RuntimeException e) {
            // Atrapa errores como "Torneo no encontrado", "No tienes suficientes puntos", etc.
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
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
    @DeleteMapping("/{torneoId}/eliminar")
    public ResponseEntity<?> eliminarTorneo(@PathVariable Long torneoId, Principal principal) {
        try {
            // Validación de seguridad: nos aseguramos de que haya un usuario logueado.
            if (principal == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No estás autenticado.");
            }

            // 1. Llamamos al método del servicio, pasándole el ID del torneo
            //    y el nombre del usuario que realiza la acción para la validación de rol.
            torneoService.eliminarTorneo(torneoId, principal.getName());

            // 2. Si la eliminación es exitosa, devolvemos una respuesta 200 OK
            //    con un mensaje de confirmación.
            return ResponseEntity.ok("Torneo eliminado exitosamente.");

        } catch (SecurityException e) {
            // CASO 1: Atrapamos específicamente el error de permisos.
            // Es una buena práctica devolver un código 403 Forbidden para este caso.
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());

        } catch (RuntimeException e) {
            // CASO 2: Atrapamos cualquier otro error de ejecución
            // (como "Torneo no encontrado") y devolvemos un 400 Bad Request.
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}