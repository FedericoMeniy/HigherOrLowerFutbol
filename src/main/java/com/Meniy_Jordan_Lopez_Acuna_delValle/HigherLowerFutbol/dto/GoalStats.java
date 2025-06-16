package com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GoalStats {
    @JsonProperty("total")
    private Integer total;

    @JsonProperty("assists")
    private Integer assists;


}