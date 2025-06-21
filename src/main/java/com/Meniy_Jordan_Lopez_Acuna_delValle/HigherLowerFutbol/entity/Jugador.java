// Archivo: src/main/java/com/Meniy_Jordan_Lopez_Acuna_delValle/HigherLowerFutbol/entity/Jugador.java

package com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Jugador implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;

    @Column(unique = true)
    private String email;

    private String password;

    private String tipoRol; // Ejemplo: "USER", "ADMIN"

    @Column(nullable = false, columnDefinition = "BIGINT DEFAULT 0")
    private int puntaje = 0;

    @OneToMany(mappedBy = "creador")
    @JsonBackReference
    private List<Torneo> torneos;


    // --- Métodos de la interfaz UserDetails ---

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Usamos el campo tipoRol para definir la autoridad del usuario.
        return List.of(new SimpleGrantedAuthority(tipoRol));
    }

    @Override
    public String getUsername() {
        // Spring Security usará este método para obtener el identificador del usuario.
        // Usamos el email porque es único y es lo que pedimos en el login.
        return email;
    }

    public String getNombreUsuario(){
        return this.username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        // Devolvemos true para indicar que la cuenta nunca expira.
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        // Devolvemos true para indicar que la cuenta no está bloqueada.
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // Devolvemos true para indicar que las credenciales no expiran.
        return true;
    }

    @Override
    public boolean isEnabled() {
        // Devolvemos true para indicar que la cuenta está habilitada.
        return true;
    }
}