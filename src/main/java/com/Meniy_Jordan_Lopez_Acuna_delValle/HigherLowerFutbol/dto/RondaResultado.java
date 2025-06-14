package com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.dto;

import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.entity.Futbolista;
import lombok.Data;

@Data

public class RondaResultado {
    private Futbolista futbolista1;
    private Futbolista futbolista2;
    private PreguntaHigherLower pregunta;
    private int valorFutbolista1;
    private int valorFutbolista2;
    private boolean booleano;
    private int diferencia;


}


