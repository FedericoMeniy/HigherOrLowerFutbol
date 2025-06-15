package com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.repository;

import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.entity.Jugador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JugadorRepository extends JpaRepository<Jugador, Long> {

    // Spring Data JPA entiende que este método debe buscar en la columna 'email'
    // y devolver true si encuentra al menos una coincidencia.
    boolean existsByEmail(String email);

    // Hacemos lo mismo para el nombre de usuario (username)
    boolean existsByUsername(String username);

    Optional<Jugador> findByEmail(String email);
    Optional<Jugador> findByUsername(String username);
}
