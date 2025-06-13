package com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.dto;

import lombok.Data;

import java.util.List;

@Data
public class PlayerStatsData {
    private PlayerDetails player;
    private List<Statistic> statistics;
}
