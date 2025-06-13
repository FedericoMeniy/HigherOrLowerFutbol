package com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor

@DiscriminatorValue("ADMIN")     /// antes estaba como "AMIGO"
public class TorneoAdmin extends Torneo{

    @Column(nullable = false, columnDefinition = "BIGINT DEFAULT 0")
    private int premio;
}
