package com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.service;

import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.enums.EstadoTorneo;
import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.exceptions.TorneoException;
import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.dto.*;
import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.entity.*;
import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.repository.DetalleTorneoRepository;
import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.repository.JugadorRepository;
import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.repository.TorneoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TorneoService {
    @Autowired
    private TorneoRepository torneoRepository;
    @Autowired
    private JugadorRepository jugadorRepository;
    @Autowired
    private DetalleTorneoRepository detalleTorneoRepository;

    public Torneo crearTorneoPrivado(TorneoDTO dto, String userNameCreador)throws TorneoException{

        Jugador creador = jugadorRepository.findByEmail(userNameCreador).orElseThrow(()-> new RuntimeException("Usuario creador no encontrado"));

        if(torneoRepository.existsByNombre("PRIVADO")){
            throw new TorneoException("El nombre del torneo que quieres ingresar ya existe");
        }
        TorneoPrivado nuevoTorneo = new TorneoPrivado();
        nuevoTorneo.setNombre(dto.getNombreTorneo());
        nuevoTorneo.setPassword(dto.getPassword());
        nuevoTorneo.setCreador(creador);
        nuevoTorneo.agregarParticipante(creador);

        LocalDateTime ahora = LocalDateTime.now();
        nuevoTorneo.setFechaCreacion(ahora);

        try{
            nuevoTorneo.setFechaFin(calcularFechaFin(nuevoTorneo.getFechaCreacion(),dto.getTiempoLimite()));
        }catch (TorneoException e){
            throw new RuntimeException("No se pudo crear el torneo: " + e.getMessage());
        }

        Torneo torneoGuardado = torneoRepository.save(nuevoTorneo);

        DetalleTorneo nuevoDetalleTorneo = new DetalleTorneo();
        nuevoDetalleTorneo.setJugador(creador);
        nuevoDetalleTorneo.setTorneo(torneoGuardado);
        detalleTorneoRepository.save(nuevoDetalleTorneo);

        return torneoGuardado;
    }

    private LocalDateTime calcularFechaFin(LocalDateTime fechaInicio, String duracion) throws TorneoException {
        if (duracion == null || duracion.isBlank()) {
            throw new TorneoException("No se establecio duracion para el torneo");
        }
        LocalDateTime fechaFin;
        switch (duracion.toLowerCase()) {
            case "30m":
                fechaFin=fechaInicio.plusMinutes(30);
                break;
            case "1h":
                fechaFin=fechaInicio.plusHours(1);
                break;
            case "3h":
                fechaFin=fechaInicio.plusHours(3);
                break;
            case "24h":
                fechaFin=fechaInicio.plusHours(24);
                break;

            default:
                throw new IllegalArgumentException("Formato de tiempo límite no válido: " + duracion);
        }
        return fechaFin;
    }

    public Torneo unirseTorneo(Long idTorneo, UnirseTorneoDTO dto, String userEmail) throws RuntimeException {

        Torneo torneo = torneoRepository.findById(idTorneo)
                .orElseThrow(() -> new RuntimeException("El torneo con id: " + idTorneo + " no fue encontrado"));

        Jugador jugador = jugadorRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Error: Usuario autenticado no encontrado en la BD."));

        if (!(torneo instanceof TorneoPrivado)) {
            throw new RuntimeException("Este método es solo para unirse a torneos privados.");
        }
        TorneoPrivado torneoPrivado = (TorneoPrivado) torneo;

        if (!torneoPrivado.getPassword().equals(dto.getPassword())) {
            throw new RuntimeException("Contraseña incorrecta");
        }

        if (torneo.getJugadores().contains(jugador)) {
            throw new RuntimeException("Este jugador ya se encuentra en el torneo.");
        }

        torneoPrivado.agregarParticipante(jugador);

        DetalleTorneo nuevoDetalleTorneo = new DetalleTorneo();
        nuevoDetalleTorneo.setTorneo(torneoPrivado);
        nuevoDetalleTorneo.setJugador(jugador);
        detalleTorneoRepository.save(nuevoDetalleTorneo);

        return torneoRepository.save(torneoPrivado);
    }

    public List<DetalleTorneo> getLeaderboard(Long torneoId) {
        if (!torneoRepository.existsById(torneoId)) {
            throw new RuntimeException("El torneo con ID " + torneoId + " no fue encontrado.");
        }
        return detalleTorneoRepository.findByTorneoIdOrderByPuntajeDescPartidasJugadasAsc(torneoId);
    }

    public List<TorneoDisponibleDTO> getTorneosDisponiblesPorTipo(String tipo, String nombreFilter) {
        List<Torneo> todosLosTorneos = torneoRepository.findAll();

        return todosLosTorneos.stream()
                .filter(torneo -> {
                    boolean matchesType = false;
                    if ("ADMIN".equalsIgnoreCase(tipo)) {
                        matchesType = torneo instanceof TorneoAdmin;
                    } else if ("PRIVADO".equalsIgnoreCase(tipo)) {
                        matchesType = torneo instanceof TorneoPrivado;
                    }

                    boolean matchesName = true;
                    if (nombreFilter != null && !nombreFilter.isBlank()) {
                        matchesName = torneo.getNombre().toLowerCase().contains(nombreFilter.toLowerCase());
                    }

                    return matchesType && matchesName;
                })
                .map(torneo -> {
                    Long id = torneo.getId();
                    String nombre = torneo.getNombre();
                    int costoPuntos = 0;
                    Integer premio = 0; // --- CAMBIO: Variable para el premio ---
                    String tipoTorneoDisplay = "";

                    if (torneo instanceof TorneoAdmin) {
                        TorneoAdmin torneoAdmin = (TorneoAdmin) torneo;
                        tipoTorneoDisplay = "ADMIN";

                        Integer costo = torneoAdmin.getCostoEntrada();
                        costoPuntos = (costo != null) ? costo : 0;

                        premio = torneoAdmin.getPremio(); // --- CAMBIO: Obtener el premio ---

                    } else if (torneo instanceof TorneoPrivado) {
                        tipoTorneoDisplay = "PRIVADO";
                    }

                    // --- CAMBIO: Pasar el premio al constructor del DTO ---
                    // (Asegúrate de que TorneoDisponibleDTO tenga el campo y constructor)
                    return new TorneoDisponibleDTO(id, nombre, tipoTorneoDisplay, costoPuntos, premio);
                })
                .collect(Collectors.toList());
    }

    public Torneo crearTorneoOficial(TorneoOficialDTO dto, String usernameCreador) throws TorneoException {
        Jugador creador = jugadorRepository.findByEmail(usernameCreador)
                .orElseThrow(() -> new RuntimeException("Usuario creador no encontrado"));

        if (!"ADMIN".equalsIgnoreCase(creador.getTipoRol())) {
            throw new SecurityException("Solo los administradores pueden crear torneos oficiales.");
        }

        TorneoAdmin nuevoTorneo = new TorneoAdmin();
        nuevoTorneo.setNombre(dto.getNombre());
        nuevoTorneo.setCreador(creador);
        nuevoTorneo.setPremio(dto.getPremio());
        nuevoTorneo.setCostoEntrada(dto.getCostoEntrada());

        LocalDateTime fechaDeInicio;

        if (dto.getHoraInicio() != null && !dto.getHoraInicio().isBlank()) {
            fechaDeInicio = LocalDateTime.parse(dto.getHoraInicio());
            nuevoTorneo.setEstadoTorneo(EstadoTorneo.PENDIENTE);
            System.out.println("Torneo programado para iniciar en: " + fechaDeInicio);
        } else {
            fechaDeInicio = LocalDateTime.now();
            nuevoTorneo.setEstadoTorneo(EstadoTorneo.ACTIVO);
            System.out.println("Torneo creado y activo inmediatamente.");
        }

        nuevoTorneo.setFechaCreacion(fechaDeInicio);
        LocalDateTime fechaDeFin = calcularFechaFin(fechaDeInicio, dto.getTiempoLimite());
        nuevoTorneo.setFechaFin(fechaDeFin);

        return torneoRepository.save(nuevoTorneo);
    }

    public Torneo unirseATorneoOficial(Long torneoId, String userEmail) {
        Torneo torneo = torneoRepository.findById(torneoId)
                .orElseThrow(() -> new RuntimeException("El torneo con ID " + torneoId + " no fue encontrado."));

        Jugador jugador = jugadorRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Error fatal: Usuario autenticado no encontrado."));

        if (!(torneo instanceof TorneoAdmin)) {
            throw new RuntimeException("Solo puedes unirte a torneos oficiales a través de esta función.");
        }
        TorneoAdmin torneoOficial = (TorneoAdmin) torneo;

        if (torneo.getEstadoTorneo() != EstadoTorneo.ACTIVO) {
            throw new RuntimeException("No te puedes unir a un torneo que ya ha finalizado.");
        }

        if (torneo.getJugadores().contains(jugador)) {
            throw new RuntimeException("Ya estás inscrito en este torneo.");
        }

        int costo = (torneoOficial.getCostoEntrada() != null) ? torneoOficial.getCostoEntrada() : 0;
        int puntosDelJugador = jugador.getPuntaje();

        if (puntosDelJugador < costo) {
            throw new RuntimeException("No tienes suficientes puntos para unirte. Necesitas " + costo + " y tienes " + puntosDelJugador + ".");
        }

        if (costo > 0) {
            jugador.setPuntaje(puntosDelJugador - costo);
            jugadorRepository.save(jugador);
        }

        torneo.agregarParticipante(jugador);
        Torneo torneoActualizado = torneoRepository.save(torneo);

        DetalleTorneo nuevoDetalleTorneo = new DetalleTorneo();
        nuevoDetalleTorneo.setTorneo(torneoActualizado);
        nuevoDetalleTorneo.setJugador(jugador);
        detalleTorneoRepository.save(nuevoDetalleTorneo);

        return torneoActualizado;
    }

    public void finalizarTorneoYRepartirPremios(Long torneoId) {
        Torneo torneo = torneoRepository.findById(torneoId)
                .orElseThrow(() -> new RuntimeException("El torneo no fue encontrado."));

        if (torneo.getEstadoTorneo() != EstadoTorneo.ACTIVO) {
            throw new RuntimeException("Este torneo ya ha sido finalizado o cancelado.");
        }

        if (!(torneo instanceof TorneoAdmin)) {
            throw new RuntimeException("Solo los torneos oficiales pueden repartir premios.");
        }
        TorneoAdmin torneoAdmin = (TorneoAdmin) torneo;
        int premioTotal = torneoAdmin.getPremio();

        List<DetalleTorneo> leaderboard = getLeaderboard(torneoId);

        if (leaderboard != null && !leaderboard.isEmpty()) {
            if (leaderboard.size() >= 1) {
                DetalleTorneo ganador = leaderboard.get(0);
                Jugador jugadorGanador = ganador.getJugador();
                int premioGanador = (int) (premioTotal * 0.50);
                jugadorGanador.setPuntaje(jugadorGanador.getPuntaje() + premioGanador);
                jugadorRepository.save(jugadorGanador);
            }

            if (leaderboard.size() >= 2) {
                DetalleTorneo segundo = leaderboard.get(1);
                Jugador jugadorSegundo = segundo.getJugador();
                int premioSegundo = (int) (premioTotal * 0.30);
                jugadorSegundo.setPuntaje(jugadorSegundo.getPuntaje() + premioSegundo);
                jugadorRepository.save(jugadorSegundo);
            }

            if (leaderboard.size() >= 3) {
                DetalleTorneo tercero = leaderboard.get(2);
                Jugador jugadorTercero = tercero.getJugador();
                int premioTercero = (int) (premioTotal * 0.20);
                jugadorTercero.setPuntaje(jugadorTercero.getPuntaje() + premioTercero);
                jugadorRepository.save(jugadorTercero);
            }
        }

        torneo.setEstadoTorneo(EstadoTorneo.FINALIZADO);
        torneoRepository.save(torneo);
    }

    public Torneo actualizarTorneoOficial(Long torneoId, ActualizarTorneoOficialDTO dto, String usernameAdmin) {
        Jugador admin = jugadorRepository.findByEmail(usernameAdmin)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado."));

        if (!"ADMIN".equalsIgnoreCase(admin.getTipoRol())) {
            throw new SecurityException("Solo los administradores pueden modificar torneos.");
        }

        Torneo torneo = torneoRepository.findById(torneoId)
                .orElseThrow(() -> new RuntimeException("El torneo no fue encontrado."));

        if (!(torneo instanceof TorneoAdmin)) {
            throw new RuntimeException("Este método solo puede modificar torneos oficiales.");
        }

        TorneoAdmin torneoAdmin = (TorneoAdmin) torneo;

        if (dto.getPremio() != null) {
            torneoAdmin.setPremio(dto.getPremio());
        }

        if (dto.getCostoEntrada() != null) {
            torneoAdmin.setCostoEntrada(dto.getCostoEntrada());
        }

        return torneoRepository.save(torneoAdmin);
    }

    public void eliminarTorneo(Long torneoId, String usernameAdmin) {
        Jugador admin = jugadorRepository.findByEmail(usernameAdmin)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado."));

        if (!"ADMIN".equalsIgnoreCase(admin.getTipoRol())) {
            throw new SecurityException("Solo los administradores pueden eliminar torneos.");
        }

        if (!torneoRepository.existsById(torneoId)) {
            throw new RuntimeException("El torneo con ID " + torneoId + " no fue encontrado.");
        }

        torneoRepository.deleteById(torneoId);
    }
}