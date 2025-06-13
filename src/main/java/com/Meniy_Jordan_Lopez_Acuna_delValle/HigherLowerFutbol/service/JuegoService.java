package com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.service;


import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.dto.PreguntaHigherLower;
import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.entity.Futbolista;
import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.entity.Jugador;
import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.repository.FutbolistaRepository;
import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.repository.JugadorRepository;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public class JuegoService {

private FutbolistaRepository FutbolistaRepository;

    private final Random random = new Random();

    public Futbolista[] elegirDosJugadoresAleatorios() {
        List<Futbolista> futbolistas = FutbolistaRepository.findAll();

        if (futbolistas.size() < 2) {
            throw new IllegalStateException("No hay suficientes futbolistas para jugar.");
        }

        Collections.shuffle(futbolistas, random);
        return new Futbolista[]{futbolistas.get(0), futbolistas.get(1)};
    }

    public void jugarRonda(Long idFutbolistaElegido) {
        Futbolista[] futbolistas = elegirDosJugadoresAleatorios();
        Futbolista f1 = futbolistas[0];
        Futbolista f2 = futbolistas[1];

        PreguntaHigherLower pregunta = PreguntaHigherLower.obtenerPregunta();


    }
    /*
 public RondaResultado jugarRonda(String eleccionMayorOMenor) {
        // 1. Elegir dos futbolistas
        Futbolista[] futbolistas = elegirDosFutbolistasAleatorios();
        Futbolista f1 = futbolistas[0];
        Futbolista f2 = futbolistas[1];

        // 2. Elegir pregunta al azar
        PreguntaHigherLower pregunta = PreguntaHigherLower.obtenerPregunta();

        // 3. Obtener valores para cada futbolista
        int valorF1 = f1.getStatistic().obtenerValorPorPregunta(pregunta);
        int valorF2 = f2.getStatistic().obtenerValorPorPregunta(pregunta);

        // 4. Evaluar respuesta del jugador
        boolean acerto;
        if ("mayor".equalsIgnoreCase(eleccionMayorOMenor)) {
            acerto = valorF2 > valorF1;
        } else if ("menor".equalsIgnoreCase(eleccionMayorOMenor)) {
            acerto = valorF2 < valorF1;
        } else {
            throw new IllegalArgumentException("Elección inválida, debe ser 'mayor' o 'menor'");
        }

        // 5. Armar resultado para devolver
        RondaResultado resultado = new RondaResultado();
        resultado.setFutbolista1(f1);
        resultado.setFutbolista2(f2);
        resultado.setPregunta(pregunta);
        resultado.setValorFutbolista1(valorF1);
        resultado.setValorFutbolista2(valorF2);
        resultado.setAcerto(acerto);

        return resultado;
    }


     */
}




