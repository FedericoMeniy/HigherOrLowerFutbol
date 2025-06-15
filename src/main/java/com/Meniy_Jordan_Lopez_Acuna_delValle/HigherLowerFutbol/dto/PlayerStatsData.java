package com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.dto;

import lombok.Data;

import java.util.List;

@Data
public class PlayerStatsData {
    private PlayerDetails player;
    private List<Statistic> statistics;

    public PlayerDetails getPlayer() {
        return player;
    }

    public void setPlayer(PlayerDetails player) {
        this.player = player;
    }

    public List<Statistic> getStatistics() {
        return statistics;
    }

    public void setStatistics(List<Statistic> statistics) {
        this.statistics = statistics;
    }

}
