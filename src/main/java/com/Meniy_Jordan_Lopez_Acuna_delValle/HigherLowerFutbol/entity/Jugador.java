package com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor

public class Jugador {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre del usuario no puede ser nulo ni estar vacío.")
    @Size(min = 2, max = 100, message = "El nombre del usuario debe tener entre 2 y 100 caracteres.")
    @Column(unique = true)
    private String username;

    @NotBlank(message = "El email no puede ser nulo ni estar vacío.")
    @Email(message = "El formato del email no es válido.")
    @Size(min = 5, max = 100, message = "El email debe tener entre 5 y 100 caracteres.")
    @Column(unique = true)
    private String email;

    @NotBlank(message = "La contraseña no puede ser nula ni estar vacía.")
    @Size(min = 8, max = 255, message = "La contraseña debe tener entre 8 y 255 caracteres.")
    private String password;

    @NotBlank(message = "El tipo de rol no puede ser nulo ni estar vacío.")
    @Size(min = 2, max = 50, message = "El tipo de rol debe tener entre 2 y 50 caracteres.")
    private String tipoRol;

    @NotNull(message = "El puntaje no puede ser nulo.")
    @Column(nullable = false, columnDefinition = "BIGINT DEFAULT 0")
    private int puntaje = 0;

    @OneToMany
    private List<Torneo> torneos;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTipoRol() {
        return tipoRol;
    }

    public void setTipoRol(String tipoRol) {
        this.tipoRol = tipoRol;
    }

    public int getPuntaje() {
        return puntaje;
    }

    public void setPuntaje(int puntaje) {
        this.puntaje = puntaje;
    }

    public List<Torneo> getTorneos() {
        return torneos;
    }

    public void setTorneos(List<Torneo> torneos) {
        this.torneos = torneos;
    }
}

