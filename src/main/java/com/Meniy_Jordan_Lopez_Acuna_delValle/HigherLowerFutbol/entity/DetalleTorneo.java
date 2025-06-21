package com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class DetalleTorneo {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JsonBackReference
    private Torneo torneo;
    @ManyToOne
    private Jugador jugador;

    private int partidasJugadas = 0;
    private int puntaje = 0;
}
