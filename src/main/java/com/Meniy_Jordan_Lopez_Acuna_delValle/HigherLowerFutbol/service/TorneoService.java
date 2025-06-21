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

    public Torneo crearTorneoPrivado(TorneoDTO dto, String userNameCreador){

        // --- LA LÍNEA QUE CAMBIAMOS ---
        // Buscamos al creador por su email, que es lo que nos llega desde el Principal de Spring Security
        Jugador creador = jugadorRepository.findByEmail(userNameCreador).orElseThrow(()-> new RuntimeException("Usuario creador no encontrado"));

        TorneoPrivado nuevoTorneo = new TorneoPrivado();
        nuevoTorneo.setNombre(dto.getNombreTorneo());
        nuevoTorneo.setPassword(dto.getPassword());
        //Inserto el dueño del torneo
        nuevoTorneo.setCreador(creador);
        //El creador es el primer jugador de su torneo
        nuevoTorneo.agregarParticipante(creador);

        //Aca establezco el tiempo limite del torneo
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
                // Si nos pasan un formato que no entendemos, lanzamos un error.
                throw new IllegalArgumentException("Formato de tiempo límite no válido: " + duracion);

        }
        return fechaFin;
    }

    // En: src/main/java/com/Meniy_Jordan_Lopez_Acuna_delValle/HigherLowerFutbol/service/TorneoService.java

    public Torneo unirseTorneo(Long idTorneo, UnirseTorneoDTO dto, String userEmail) throws RuntimeException {

        // 1. Buscamos el Torneo por su ID (esto ya estaba correcto).
        Torneo torneo = torneoRepository.findById(idTorneo)
                .orElseThrow(() -> new RuntimeException("El torneo con id: " + idTorneo + " no fue encontrado"));

        // 2. ¡CORRECCIÓN IMPORTANTE! Usamos el email para buscar al Jugador.
        // Ahora la variable 'jugador' sí existe y contiene al usuario correcto.
        Jugador jugador = jugadorRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Error: Usuario autenticado no encontrado en la BD."));

        // 3. ¡CORRECCIÓN DE SEGURIDAD! Verificamos que el torneo sea de tipo "Privado"
        // antes de intentar acceder a su contraseña. Esto evita errores de casteo.
        if (!(torneo instanceof TorneoPrivado)) {
            throw new RuntimeException("Este método es solo para unirse a torneos privados.");
        }
        TorneoPrivado torneoPrivado = (TorneoPrivado) torneo;

        // 4. Validamos la contraseña (esto ya estaba correcto).
        if (!torneoPrivado.getPassword().equals(dto.getPassword())) {
            throw new RuntimeException("Contraseña incorrecta");
        }

        // 5. Verificamos si el jugador ya está en el torneo.
        if (torneo.getJugadores().contains(jugador)) {
            throw new RuntimeException("Este jugador ya se encuentra en el torneo.");
        }

        // 6. Si todas las validaciones pasan, agregamos al participante.
        torneoPrivado.agregarParticipante(jugador);

        // 7. Creamos su registro en DetalleTorneo para llevar su puntaje.
        DetalleTorneo nuevoDetalleTorneo = new DetalleTorneo();
        nuevoDetalleTorneo.setTorneo(torneoPrivado);
        nuevoDetalleTorneo.setJugador(jugador);
        detalleTorneoRepository.save(nuevoDetalleTorneo);


        // 8. Guardamos el torneo con la lista de jugadores actualizada.
        return torneoRepository.save(torneoPrivado);
    }

    public List<DetalleTorneo> getLeaderboard(Long torneoId) {
        // Simplemente validamos que el torneo exista antes de buscar el leaderboard
        if (!torneoRepository.existsById(torneoId)) {
            throw new RuntimeException("El torneo con ID " + torneoId + " no fue encontrado.");
        }
        return detalleTorneoRepository.findByTorneoIdOrderByPuntajeDescPartidasJugadasAsc(torneoId);
    }

    public List<TorneoDisponibleDTO> getTorneosDisponiblesPorTipo(String tipo) {
        // 1. Obtenemos todos los torneos del repositorio.
        List<Torneo> todosLosTorneos = torneoRepository.findAll();

        // 2. Filtramos y mapeamos la lista a nuestro DTO.
        return todosLosTorneos.stream()
                .filter(torneo -> {
                    // Filtramos por el tipo de torneo solicitado.
                    if ("ADMIN".equalsIgnoreCase(tipo)) {
                        return torneo instanceof TorneoAdmin;
                    } else if ("PRIVADO".equalsIgnoreCase(tipo)) {
                        // Importante: en la BBDD el tipo es "AMIGO"
                        return torneo instanceof TorneoPrivado;
                    }
                    return false;
                })
                .map(torneo -> {
                    // 3. Transformamos cada entidad Torneo al DTO que necesita el frontend.
                    Long id = torneo.getId();
                    String nombre = torneo.getNombre();
                    int costoPuntos = 0;
                    String tipoTorneo = "";

                    if (torneo instanceof TorneoAdmin) {
                        tipoTorneo = "ADMIN";
                        costoPuntos = ((TorneoAdmin) torneo).getPremio(); // En el backend se llama 'premio'.
                    } else if (torneo instanceof TorneoPrivado) {
                        tipoTorneo = "PRIVADO"; // Mapeamos "AMIGO" a "PRIVADO" para el frontend.
                    }

                    return new TorneoDisponibleDTO(id, nombre, tipoTorneo, costoPuntos);
                })
                .collect(Collectors.toList());
    }
    public Torneo crearTorneoOficial(TorneoOficialDTO dto, String usernameCreador) throws TorneoException {

        // 1. Validamos que el creador exista (lógica reutilizada)
        Jugador creador = jugadorRepository.findByEmail(usernameCreador)
                .orElseThrow(() -> new RuntimeException("Usuario creador no encontrado"));

        // 2. ¡VALIDACIÓN DE SEGURIDAD IMPORTANTE!
        // Verificamos que el rol del jugador sea "ADMIN".
        if (!"ADMIN".equalsIgnoreCase(creador.getTipoRol())) {
            throw new SecurityException("Solo los administradores pueden crear torneos oficiales.");
        }

        // 3. Creamos una instancia de la entidad específica: TorneoAdmin
        TorneoAdmin nuevoTorneo = new TorneoAdmin();
        nuevoTorneo.setNombre(dto.getNombre());
        nuevoTorneo.setCreador(creador);
        nuevoTorneo.setPremio(dto.getPremio());
        nuevoTorneo.setCostoEntrada(dto.getCostoEntrada());
        nuevoTorneo.setEstadoTorneo(EstadoTorneo.ACTIVO);

        // 4. Reutilizamos la lógica de las fechas que ya funciona
        LocalDateTime ahora = LocalDateTime.now();
        nuevoTorneo.setFechaCreacion(ahora);
        nuevoTorneo.setFechaFin(calcularFechaFin(ahora, dto.getTiempoLimite()));

        // 5. Guardamos el nuevo torneo. Hibernate sabrá que debe poner "ADMIN"
        // en la columna 'tipo_torneo' gracias a la anotación @DiscriminatorValue.
        return torneoRepository.save(nuevoTorneo);
    }
    public Torneo unirseATorneoOficial(Long torneoId, String usernameJugador) {

        // 1. Buscamos el torneo y el jugador (lógica reutilizada).
        Torneo torneo = torneoRepository.findById(torneoId)
                .orElseThrow(() -> new RuntimeException("El torneo con ID " + torneoId + " no fue encontrado."));

        Jugador jugador = jugadorRepository.findByUsername(usernameJugador)
                .orElseThrow(() -> new RuntimeException("Error fatal: Usuario autenticado no encontrado."));

        // 2. Validamos que el torneo sea un TorneoAdmin.
        if (!(torneo instanceof TorneoAdmin)) {
            throw new RuntimeException("Solo puedes unirte a torneos oficiales a través de esta función.");
        }
        TorneoAdmin torneoOficial = (TorneoAdmin) torneo;

        // 3. Validamos que el torneo esté ACTIVO (¡usando nuestro enum!).
        if (torneo.getEstadoTorneo() != EstadoTorneo.ACTIVO) {
            throw new RuntimeException("No te puedes unir a un torneo que ya ha finalizado.");
        }

        // 4. Verificamos que el jugador no esté ya en el torneo.
        if (torneo.getJugadores().contains(jugador)) {
            throw new RuntimeException("Ya estás inscrito en este torneo.");
        }

        // 5. --- ¡LÓGICA NUEVA Y CLAVE: VERIFICAR Y COBRAR PUNTOS! ---
        int costo = torneoOficial.getCostoEntrada();
        int puntosDelJugador = jugador.getPuntaje();

        if (puntosDelJugador < costo) {
            throw new RuntimeException("No tienes suficientes puntos para unirte. Necesitas " + costo + " y tienes " + puntosDelJugador + ".");
        }

        // Si tiene suficientes puntos, se los restamos de su puntaje GLOBAL.
        jugador.setPuntaje(puntosDelJugador - costo);
        jugadorRepository.save(jugador); // Guardamos al jugador con su nuevo total de puntos.

        // 6. Si todas las validaciones pasan, lo añadimos al torneo.
        torneo.agregarParticipante(jugador);
        Torneo torneoActualizado = torneoRepository.save(torneo);

        // 7. Creamos su entrada en la tabla de puntajes (DetalleTorneo).
        DetalleTorneo nuevoDetalleTorneo = new DetalleTorneo();
        nuevoDetalleTorneo.setTorneo(torneoActualizado);
        nuevoDetalleTorneo.setJugador(jugador);
        detalleTorneoRepository.save(nuevoDetalleTorneo);

        return torneoActualizado;
    }
    public void finalizarTorneoYRepartirPremios(Long torneoId) {
        // 1. Buscamos el torneo y validamos
        Torneo torneo = torneoRepository.findById(torneoId)
                .orElseThrow(() -> new RuntimeException("El torneo no fue encontrado."));

        // --- CAMBIO CLAVE 1: Comparación con Enum ---
        // Ahora comparamos directamente con el valor del enum.
        // Es más seguro (sin errores de tipeo) y más claro.
        if (torneo.getEstadoTorneo() != EstadoTorneo.ACTIVO) {
            throw new RuntimeException("Este torneo ya ha sido finalizado o cancelado.");
        }

        // Verificamos que sea un TorneoAdmin para poder obtener el premio
        if (!(torneo instanceof TorneoAdmin)) {
            throw new RuntimeException("Solo los torneos oficiales pueden repartir premios.");
        }
        TorneoAdmin torneoAdmin = (TorneoAdmin) torneo;
        int premioTotal = torneoAdmin.getPremio();

        // 2. Obtenemos la tabla de posiciones final (esta parte no cambia)
        List<DetalleTorneo> leaderboard = getLeaderboard(torneoId);

        // 3. Repartimos los premios (esta lógica no cambia)
        if (leaderboard != null && !leaderboard.isEmpty()) {

            // --- Premio para el 1er Puesto ---
            if (leaderboard.size() >= 1) {
                DetalleTorneo ganador = leaderboard.get(0);
                Jugador jugadorGanador = ganador.getJugador();
                int premioGanador = (int) (premioTotal * 0.50); // 50%

                jugadorGanador.setPuntaje(jugadorGanador.getPuntaje() + premioGanador);
                jugadorRepository.save(jugadorGanador);
            }

            // --- Premio para el 2do Puesto ---
            if (leaderboard.size() >= 2) {
                DetalleTorneo segundo = leaderboard.get(1);
                Jugador jugadorSegundo = segundo.getJugador();
                int premioSegundo = (int) (premioTotal * 0.30); // 30%

                jugadorSegundo.setPuntaje(jugadorSegundo.getPuntaje() + premioSegundo);
                jugadorRepository.save(jugadorSegundo);
            }

            // --- Premio para el 3er Puesto ---
            if (leaderboard.size() >= 3) {
                DetalleTorneo tercero = leaderboard.get(2);
                Jugador jugadorTercero = tercero.getJugador();
                int premioTercero = (int) (premioTotal * 0.20); // 20%

                jugadorTercero.setPuntaje(jugadorTercero.getPuntaje() + premioTercero);
                jugadorRepository.save(jugadorTercero);
            }
        }

        torneo.setEstadoTorneo(EstadoTorneo.FINALIZADO);
        torneoRepository.save(torneo);
    }
    public Torneo actualizarTorneoOficial(Long torneoId, ActualizarTorneoOficialDTO dto, String usernameAdmin) {
        // 1. Validamos que el admin exista
        Jugador admin = jugadorRepository.findByUsername(usernameAdmin)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado."));

        // 2. ¡VALIDACIÓN DE SEGURIDAD!
        if (!"ADMIN".equalsIgnoreCase(admin.getTipoRol())) {
            throw new SecurityException("Solo los administradores pueden modificar torneos.");
        }

        // 3. Buscamos el torneo y validamos que sea un TorneoAdmin
        Torneo torneo = torneoRepository.findById(torneoId)
                .orElseThrow(() -> new RuntimeException("El torneo no fue encontrado."));

        if (!(torneo instanceof TorneoAdmin)) {
            throw new RuntimeException("Este método solo puede modificar torneos oficiales.");
        }

        TorneoAdmin torneoAdmin = (TorneoAdmin) torneo;

        if (dto.getPremio() != null) {

            torneoAdmin.setPremio(dto.getPremio());
        }


        // Actualizar el costo de puntos:
        if (dto.getCostoEntrada() != null) {
            torneoAdmin.setCostoEntrada(dto.getCostoEntrada());
        }

        // 5. Guardamos el torneo actualizado con solo los cambios aplicados.
        return torneoRepository.save(torneoAdmin);
    }
    public void eliminarTorneo(Long torneoId, String usernameAdmin) {
        // 1. Validamos que el usuario que ejecuta la acción sea un ADMIN.
        // Esta lógica es crucial para la seguridad.
        Jugador admin = jugadorRepository.findByUsername(usernameAdmin)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado."));

        // Usando el enum para una comparación segura.
        if (!"ADMIN".equalsIgnoreCase(admin.getTipoRol())) {
            throw new SecurityException("Solo los administradores pueden eliminar torneos.");
        }

        // 2. Verificamos que el torneo realmente exista antes de intentar borrarlo.
        // Esto evita errores si se intenta borrar un ID que ya no está.
        if (!torneoRepository.existsById(torneoId)) {
            throw new RuntimeException("El torneo con ID " + torneoId + " no fue encontrado.");
        }

        // 3. ¡Y LISTO! Simplemente borramos el torneo por su ID.
        // Gracias a la configuración de cascada en la entidad Torneo, Hibernate se encarga del resto.
        torneoRepository.deleteById(torneoId);
    }

}
