package com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.dto;

import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.entity.Jugador;
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
    private String nombreTorneo;
    private String password;
    private String tiempoLimite;
}