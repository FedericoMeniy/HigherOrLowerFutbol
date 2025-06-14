package com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.dto;
import lombok.Data;

import java.util.Random;

public enum PreguntaHigherLower {

    MAS_GOLES("¿Tiene más goles?"),
    MAS_ASISTENCIAS("¿Tiene más asistencias?"),
    MAS_TARJETAS_ROJAS("¿Tiene más tarjetas rojas?"),
    MAS_TARJETAS_AMARILLAS("¿Tiene más tarjetas amarillas?"),
    MAS_PARTIDOS_JUGADOS("¿Tiene más partidos jugados?");

    private final String textoPregunta;

    PreguntaHigherLower(String textoPregunta) {
        this.textoPregunta = textoPregunta;
    }

    public String getTextoPregunta() {
        return textoPregunta;
    }

  public static PreguntaHigherLower obtenerPregunta () {
        PreguntaHigherLower[] valores = values();
        return valores[new Random().nextInt(valores.length)];
  }

}

