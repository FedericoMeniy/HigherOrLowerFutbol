package com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JugadorDTO {

    private Long id;
    private String nombre;
    private String email;
    private String password;
    private String tipoRol;
    private Long puntaje;


}
