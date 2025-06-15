package com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)     /// Ignora los datos que no queremos para el futbolista
public class FutbolistaDTO {

    @JsonProperty("id")         /// No existe "idApi" cuando haces el llamado, seria id y supongo que con idApi querian mostrar
    private Integer idApi;      /// la id del jugador de la api

    @JsonProperty("name")       /// mas o menos lo mismo
    private String nombre;

    @JsonProperty("photo")
    private String imagen;

    private int goles;
    private int asistencias;
    private int tarjetasRojas;
    private int tarjetasAmarillas;
}
