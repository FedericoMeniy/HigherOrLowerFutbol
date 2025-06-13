package com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.dto;

import lombok.Data;

@Data
public class FutbolistaDTO {

    private Integer idApi;
    private String nombre;
    private String imagen;
    private int goles;
    private int asistencias;
    private int tarjetasRojas;
    private int tarjetasAmarillas;
}
