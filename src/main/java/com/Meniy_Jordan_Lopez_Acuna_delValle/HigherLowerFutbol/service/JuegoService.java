package com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.service;

import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.dto.PreguntaHigherLower;
import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.dto.RondaResultado;
import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.entity.Estadistica;
import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.entity.Futbolista;
import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.repository.FutbolistaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@Service
public class JuegoService {

    @Autowired
    private FutbolistaRepository futbolistaRepository;

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
}