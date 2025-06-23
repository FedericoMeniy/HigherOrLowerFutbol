package com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.service;

import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.dto.PartidaTorneoDTO;
import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.dto.PreguntaHigherLower;
import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.dto.RondaResultado;
import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.entity.*;
import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.repository.DetalleTorneoRepository;
import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.repository.FutbolistaRepository;
import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.repository.JugadorRepository;
import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.repository.TorneoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@Service
public class JuegoService {

    @Autowired
    private FutbolistaRepository futbolistaRepository;
    @Autowired
    private JugadorRepository jugadorRepository;
    @Autowired
    private TorneoRepository torneoRepository;
    @Autowired
    private DetalleTorneoRepository detalleTorneoRepository;

    private final Random random = new Random();

    public RondaResultado jugarRonda(String eleccionDelUsuario) {

        List<Futbolista> futbolistas = futbolistaRepository.findAll();
        if (futbolistas.size() < 2) {
            throw new IllegalStateException("No hay suficientes futbolistas en la BD para jugar.");
        }
        Collections.shuffle(futbolistas);

        Futbolista f1 = futbolistas.get(0);
        Futbolista f2 = futbolistas.get(1);


        PreguntaHigherLower pregunta = PreguntaHigherLower.obtenerPregunta();

        int valorF1 = obtenerValorPorPregunta(f1, pregunta);
        int valorF2 = obtenerValorPorPregunta(f2, pregunta);

        boolean esCorrecto;
        if ("mayor".equalsIgnoreCase(eleccionDelUsuario)) {
            esCorrecto = valorF2 > valorF1;
        } else if ("menor".equalsIgnoreCase(eleccionDelUsuario)) {
            esCorrecto = valorF2 < valorF1;
        } else {

            esCorrecto = false;
        }

        RondaResultado resultado = new RondaResultado();
        resultado.setFutbolista1(f1);
        resultado.setFutbolista2(f2);
        resultado.setPregunta(pregunta);
        resultado.setValorFutbolista1(valorF1);
        resultado.setValorFutbolista2(valorF2);
        resultado.setBooleano(esCorrecto);

        return resultado;
    }

    private int obtenerValorPorPregunta(Futbolista futbolista, PreguntaHigherLower pregunta) {
        Estadistica estadistica = futbolista.getEstadistica();
        if (estadistica == null) {
            return 0;
        }

        switch (pregunta) {
            case MAS_GOLES:
                return estadistica.getGoles();
            case MAS_ASISTENCIAS:
                return estadistica.getAsistencias();
            case MAS_TARJETAS_ROJAS:
                return estadistica.getTarjetasRojas();
            case MAS_TARJETAS_AMARILLAS:
                return estadistica.getTarjetasAmarillas();
            default:
                return 0;
        }
    }

    public RondaResultado generarRondaParaTorneo() {
        List<Futbolista> futbolistas = futbolistaRepository.findAll();
        if (futbolistas.size() < 2) {
            throw new IllegalStateException("No hay suficientes futbolistas en la BD para jugar.");
        }
        Collections.shuffle(futbolistas);

        Futbolista f1 = futbolistas.get(0);
        Futbolista f2 = futbolistas.get(1);

        PreguntaHigherLower pregunta = PreguntaHigherLower.obtenerPregunta();
        int valorF1 = obtenerValorPorPregunta(f1, pregunta);
        int valorF2 = obtenerValorPorPregunta(f2, pregunta);

        RondaResultado resultado = new RondaResultado();
        resultado.setFutbolista1(f1);
        resultado.setFutbolista2(f2);
        resultado.setPregunta(pregunta);
        resultado.setValorFutbolista1(valorF1);
        resultado.setValorFutbolista2(valorF2);

        return resultado;
    }
    public DetalleTorneo registrarResultadoPartida(PartidaTorneoDTO partidaDTO) {

        Torneo torneo = torneoRepository.findById(partidaDTO.getIdTorneo())
                .orElseThrow(() -> new RuntimeException("Torneo no encontrado con ID: " + partidaDTO.getIdJugador()));

        Jugador jugador = jugadorRepository.findById(partidaDTO.getIdJugador())
                .orElseThrow(() -> new RuntimeException("Jugador no encontrado con ID: " + partidaDTO.getIdJugador()));


        DetalleTorneo puntaje = detalleTorneoRepository.findByTorneoAndJugador(torneo, jugador)
                .orElseThrow(() -> new RuntimeException("El jugador no está inscrito en este torneo."));

        if (puntaje.getPartidasJugadas() >= 10) {
            throw new RuntimeException("Ya has jugado tus 10 partidas para este torneo.");
        }

        puntaje.setPartidasJugadas(puntaje.getPartidasJugadas() + 1);

        if (partidaDTO.getPuntosObtenidos() > 0) {
            puntaje.setPuntaje(puntaje.getPuntaje() + partidaDTO.getPuntosObtenidos());
        }

        return detalleTorneoRepository.save(puntaje);
    }
}