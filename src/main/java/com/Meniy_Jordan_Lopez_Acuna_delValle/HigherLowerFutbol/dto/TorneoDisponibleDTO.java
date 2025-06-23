package com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.dto;// Archivo: src/main/java/com/Meniy_Jordan_Lopez_Acuna_delValle/HigherLowerFutbol/dto/TorneoDisponibleDTO.java

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TorneoDisponibleDTO {
    private Long id;
    private String nombre;
    private String tipo;
    private int costoPuntos;
    private Integer premio;
}