package com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.dto;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TorneoDTO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre del torneo no puede ser nulo")
    @Size(min = 2, max = 100, message = "El nombre del torneo debe tener entre 2 y 100 caracteres")
    private String nombre;

    private List<Long> jugadorIds; // ??? RELACION

}