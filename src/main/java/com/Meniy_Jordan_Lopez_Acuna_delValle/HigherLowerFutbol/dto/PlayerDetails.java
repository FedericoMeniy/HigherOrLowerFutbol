package com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PlayerDetails {
    private Integer id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("photo")
    private String photo;

}