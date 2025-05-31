package com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
@Entity

public class Torneo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre del torneo no puede ser nulo")
    @Size(min = 2, max = 100, message = "El nombre del torneo debe tener entre 2 y 100 caracteres")
    private String nombre;

    @ManyToMany
    private List<Jugador> jugadores;
    private int premio;

}
