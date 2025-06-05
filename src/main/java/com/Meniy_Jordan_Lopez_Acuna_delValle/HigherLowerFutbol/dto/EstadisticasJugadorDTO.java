package com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString

public class EstadisticasJugadorDTO {
    //Manejo de datos API

    private String liga;
    private int goles;
    private int asistencias;
    private int tarjetasAmarillas;
    private int tarjetasRojas;


}
