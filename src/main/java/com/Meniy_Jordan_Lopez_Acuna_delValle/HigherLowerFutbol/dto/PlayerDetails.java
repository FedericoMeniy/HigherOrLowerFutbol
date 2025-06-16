package com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.dto;

// Importa JsonProperty
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PlayerDetails {
    private Integer id; // "id" probablemente coincide, así que no necesita anotación

    @JsonProperty("name") // Mapea el JSON "name" al campo "name"
    private String name;

    @JsonProperty("photo") // Mapea el JSON "photo" al campo "photo"
    private String photo;

    // Getters y Setters (Lombok los genera con @Data)
}