package com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.entity;

import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.enums.Tematica;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor

public class HigherOrLower {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Tematica tematica;
    private String entity1;
    private String entity2;
    private String imagenEntity1;
    private String imagenEntity2;
    private int valor1;
    private int valor2;
}
