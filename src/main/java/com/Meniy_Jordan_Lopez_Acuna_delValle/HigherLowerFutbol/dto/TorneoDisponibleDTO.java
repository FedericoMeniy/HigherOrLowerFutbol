package com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TorneoDisponibleDTO {
    private Long id;
    private String nombre;
    private String tipo; // "ADMIN" o "PRIVADO"
    private int costoPuntos;
}