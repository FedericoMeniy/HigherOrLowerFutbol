package com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.repository;

import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.entity.Jugador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JugadorRepository extends JpaRepository<Jugador, Long> {


    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    Optional<Jugador> findByEmail(String email);
    Optional<Jugador> findByUsername(String username);
}
