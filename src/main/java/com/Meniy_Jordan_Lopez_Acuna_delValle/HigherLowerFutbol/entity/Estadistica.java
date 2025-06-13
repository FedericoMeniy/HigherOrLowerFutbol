package com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity

public class Estadistica {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int goles;
    private int asistencias;
    private int tarjetasRojas;
    private int tarjetasAmarillas;

    //nahuelcorneta
}
