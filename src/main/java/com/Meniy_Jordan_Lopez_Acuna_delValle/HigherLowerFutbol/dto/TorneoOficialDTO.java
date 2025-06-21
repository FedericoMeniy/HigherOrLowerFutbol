package com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.dto;

import lombok.Data;

@Data
public class TorneoOficialDTO {
    private String nombre;
    private String tiempoLimite;
    private String horaInicio;
    private Integer premio;
    private Integer costoEntrada;
}
