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

    /**
     * Prepara y devuelve una ronda del juego.
     *
     * @param eleccionDelUsuario La elección del usuario ("mayor" o "menor").
     * @return Un objeto RondaResultado con los datos de la ronda.
     */
    public RondaResultado jugarRonda(String eleccionDelUsuario) {
        // 1. Obtener todos los futbolistas de la base de datos.
        List<Futbolista> futbolistas = futbolistaRepository.findAll();
        if (futbolistas.size() < 2) {
            throw new IllegalStateException("No hay suficientes futbolistas en la BD para jugar.");
        }
        // 2. Mezclarlos para seleccionar dos al azar.
        Collections.shuffle(futbolistas);

        Futbolista f1 = futbolistas.get(0);
        Futbolista f2 = futbolistas.get(1);

        // 3. Elegir una pregunta al azar (ej: ¿Tiene más goles?).
        PreguntaHigherLower pregunta = PreguntaHigherLower.obtenerPregunta();

        // 4. Obtener el valor de la estadística para cada futbolista.
        int valorF1 = obtenerValorPorPregunta(f1, pregunta);
        int valorF2 = obtenerValorPorPregunta(f2, pregunta);

        // 5. Evaluar si la elección del usuario fue correcta.
        boolean esCorrecto;
        if ("mayor".equalsIgnoreCase(eleccionDelUsuario)) {
            esCorrecto = valorF2 > valorF1;
        } else if ("menor".equalsIgnoreCase(eleccionDelUsuario)) {
            esCorrecto = valorF2 < valorF1;
        } else {
            // Se puede lanzar una excepción si la elección no es válida.
            // Por simplicidad, asumimos que no es correcto.
            esCorrecto = false;
        }

        // 6. Preparar el objeto de respuesta para el frontend.
        RondaResultado resultado = new RondaResultado();
        resultado.setFutbolista1(f1);
        resultado.setFutbolista2(f2);
        resultado.setPregunta(pregunta);
        resultado.setValorFutbolista1(valorF1);
        resultado.setValorFutbolista2(valorF2);
        resultado.setBooleano(esCorrecto); // 'booleano' indica si el usuario acertó

        return resultado;
    }

    /**
     * Método auxiliar para obtener el valor de una estadística específica de un futbolista.
     *
     * @param futbolista El futbolista del cual se extraerá la estadística.
     * @param pregunta  El tipo de estadística a obtener.
     * @return El valor numérico de la estadística.
     */
    private int obtenerValorPorPregunta(Futbolista futbolista, PreguntaHigherLower pregunta) {
        Estadistica estadistica = futbolista.getEstadistica();
        if (estadistica == null) {
            return 0; // Devuelve 0 si un futbolista no tiene estadísticas registradas
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
            // La pregunta MAS_PARTIDOS_JUGADOS existe en el enum pero no hay un campo en la entidad Estadistica.
            // Se puede añadir el campo o manejar este caso.
            case MAS_PARTIDOS_JUGADOS:
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

        // 1. Buscamos las entidades principales a partir de los IDs que nos llegaron en el DTO.
        // Si no se encuentra alguno, lanzamos un error y la operación se detiene.
        Torneo torneo = torneoRepository.findById(partidaDTO.getIdTorneo())
                .orElseThrow(() -> new RuntimeException("Torneo no encontrado con ID: " + partidaDTO.getIdJugador()));

        Jugador jugador = jugadorRepository.findById(partidaDTO.getIdJugador())
                .orElseThrow(() -> new RuntimeException("Jugador no encontrado con ID: " + partidaDTO.getIdJugador()));

        // 2. Usamos el método personalizado del repositorio para encontrar el "marcador" específico
        // que conecta a este jugador con este torneo.
        DetalleTorneo puntaje = detalleTorneoRepository.findByTorneoAndJugador(torneo, jugador)
                .orElseThrow(() -> new RuntimeException("El jugador no está inscrito en este torneo."));

        // 3. Validamos la regla de negocio: ¿El jugador ya ha jugado sus 10 partidas?
        if (puntaje.getPartidasJugadas() >= 10) {
            throw new RuntimeException("Ya has jugado tus 10 partidas para este torneo.");
        }

        // 4. Si todo está en orden, actualizamos los datos del objeto en memoria.
        // Primero, incrementamos el contador de partidas jugadas.
        puntaje.setPartidasJugadas(puntaje.getPartidasJugadas() + 1);

        // Segundo, sumamos los puntos obtenidos en esta partida al total acumulado.
        if (partidaDTO.getPuntosObtenidos() > 0) {
            puntaje.setPuntaje(puntaje.getPuntaje() + partidaDTO.getPuntosObtenidos());
        }

        // 5. Finalmente, guardamos el objeto 'puntaje' actualizado en la base de datos
        // y lo devolvemos para que el controlador pueda enviarlo como respuesta.
        return detalleTorneoRepository.save(puntaje);
    }
}