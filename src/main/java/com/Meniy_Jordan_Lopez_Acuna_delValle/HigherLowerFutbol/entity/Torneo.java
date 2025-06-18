package com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor

@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_torneo")

public class Torneo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre del torneo no puede ser nulo")
    @Size(min = 2, max = 100, message = "El nombre del torneo debe tener entre 2 y 100 caracteres")
    private String nombre;

    @ManyToMany
    @JoinTable(
            name = "torneo_jugador",
            joinColumns = @JoinColumn(name = "torneo_id"),
            inverseJoinColumns = @JoinColumn(name = "jugador_id")     /// Probando cosas
    )
    private List<Jugador> jugadores;


    @ManyToOne
    private Jugador creador;

    private LocalDateTime fechaCreacion;

    private LocalDateTime fechaFin;

    public void setCreador(Jugador creador) {
        this.creador = creador;
    }
}
