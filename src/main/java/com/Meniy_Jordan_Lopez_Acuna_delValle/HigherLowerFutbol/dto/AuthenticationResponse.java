// Archivo: src/main/java/com/Meniy_Jordan_Lopez_Acuna_delValle/HigherLowerFutbol/dto/AuthenticationResponse.java

package com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {
    private String token;
}