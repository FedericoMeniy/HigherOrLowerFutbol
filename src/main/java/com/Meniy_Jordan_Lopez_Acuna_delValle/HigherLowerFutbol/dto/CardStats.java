package com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CardStats {
    @JsonProperty("red")
    private Integer red;

    @JsonProperty("yellow")
    private Integer yellow;

    @JsonProperty("yellowred")
    private Integer yellowred;

}