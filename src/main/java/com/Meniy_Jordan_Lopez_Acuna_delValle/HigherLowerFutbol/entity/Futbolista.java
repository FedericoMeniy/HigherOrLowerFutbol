package com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

public class Futbolista {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer idApi;
    private String nombre;
    private String imagenURL;
    private Integer goles;
    private Integer asistencias;
    private Integer tarjetasRojas;
    private Integer tarjetasAmarillas;
    private Integer partidosJugados;

//2. Atributo idApi (ID del jugador en la API externa):
//private Integer idApi;: Sí, esto es ABSOLUTAMENTE CRUCIAL y está muy bien.

    //A casa fede bot
}
