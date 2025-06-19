package com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.service;

import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.Exceptions.TorneoException;
import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.dto.TorneoDTO;
import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.dto.UnirseTorneoDTO;
import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.entity.DetalleTorneo;
import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.entity.Jugador;
import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.entity.Torneo;
import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.entity.TorneoPrivado;
import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.repository.DetalleTorneoRepository;
import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.repository.JugadorRepository;
import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.repository.TorneoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TorneoService {
    @Autowired
    private TorneoRepository torneoRepository;
    @Autowired
    private JugadorRepository jugadorRepository;
    @Autowired
    private DetalleTorneoRepository detalleTorneoRepository;


    public Torneo crearTorneoPrivado(TorneoDTO dto, String userNameCreador) throws TorneoException{

        Jugador creador = jugadorRepository.findByUsername(userNameCreador).orElseThrow(()-> new RuntimeException("Usuario creador no encontrado"));

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

        DetalleTorneo nuevoDetalleTorneo = new DetalleTorneo();
        nuevoDetalleTorneo.setJugador(creador);
        nuevoDetalleTorneo.setTorneo(nuevoTorneo);
        detalleTorneoRepository.save(nuevoDetalleTorneo);

            nuevoTorneo.setFechaFin(calcularFechaFin(nuevoTorneo.getFechaFin(),dto.getTiempoLimite()));

        return torneoRepository.save(nuevoTorneo);
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

    private Torneo unirseTorneo(Long idTorneo, UnirseTorneoDTO dto) throws RuntimeException{

        Torneo torneo = torneoRepository.findById(idTorneo).orElseThrow(()-> new RuntimeException("El torneo con id:" + idTorneo + "no fue encontrado"));
        Jugador jugador = jugadorRepository.findById(dto.getIdJugador()).orElseThrow(()-> new RuntimeException("El jugador con id:"+ dto.getIdJugador()+ "No existe"));

        TorneoPrivado torneoPrivado = (TorneoPrivado) torneo;
        if(!torneoPrivado.getPassword().equals(dto.getPassword())){

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

        // Guardamos el torneo con la lista de jugadores actualizada.
        return torneoRepository.save(torneoPrivado);
    }
    public List<DetalleTorneo> getLeaderboard(Long torneoId) {
        // Simplemente validamos que el torneo exista antes de buscar el leaderboard
        if (!torneoRepository.existsById(torneoId)) {
            throw new RuntimeException("El torneo con ID " + torneoId + " no fue encontrado.");
        }
        return detalleTorneoRepository.findByTorneoIdOrderByPuntajeDescPartidasJugadasAsc(torneoId);
    }


}
