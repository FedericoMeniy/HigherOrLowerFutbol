package com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.dto;

import lombok.Data;

@Data
// Renombrar la clase a CardStats
public class CardStats {
    //Integer para manejar atributos null
    private Integer red;
    private Integer yellow;
    private Integer yellowred;

    public Integer getRed() {
        return red;
    }

    public void setRed(Integer red) {
        this.red = red;
    }

    public Integer getYellow() {
        return yellow;
    }

    public void setYellow(Integer yellow) {
        this.yellow = yellow;
    }

    public Integer getYellowred() {
        return yellowred;
    }

    public void setYellowred(Integer yellowred) {
        this.yellowred = yellowred;
    }
}
