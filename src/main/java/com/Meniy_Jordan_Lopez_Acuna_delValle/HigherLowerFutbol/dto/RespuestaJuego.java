package com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.dto;

import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.entity.Futbolista;
import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.entity.Jugador;
import lombok.Data;

@Data
public class RespuestaJuego {
    private Futbolista futbolistaActual;
    private Futbolista futbolistaSiguiente;
    private String estadisticaActual;
}