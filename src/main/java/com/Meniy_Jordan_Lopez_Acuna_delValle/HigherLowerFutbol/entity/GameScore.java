package com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.entity;

import lombok.Data;

@Data
public class GameScore {
    private int puntos;

    public void sumar(int cantidad) {
        puntos += cantidad;
    }

    public void restablecer() {
        puntos = 0;
    }
}
