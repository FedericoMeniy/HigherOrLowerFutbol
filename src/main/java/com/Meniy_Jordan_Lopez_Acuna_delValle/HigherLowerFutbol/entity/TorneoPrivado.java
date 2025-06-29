package com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor

@DiscriminatorValue("AMIGO")
public class TorneoPrivado extends Torneo{

    @NotBlank(message = "La contraseña no puede ser nulo")
    @Size(min = 2, max = 100, message = "La contraseña debe tener entre 2 y 100 caracteres")
    private String password;


}
