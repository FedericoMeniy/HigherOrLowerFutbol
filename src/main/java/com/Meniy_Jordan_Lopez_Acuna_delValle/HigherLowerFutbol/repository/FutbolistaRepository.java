package com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.repository;

import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.entity.Futbolista;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FutbolistaRepository extends JpaRepository<Futbolista, Long> {

     Optional<Futbolista> findByIdApi(Integer idApi);

}
