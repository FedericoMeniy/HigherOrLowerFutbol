package com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.dto;

import lombok.Data;

import java.util.List;

@Data
public class PlayerStatsApiResponse {
    private List<PlayerStatsData> response;

    public List<PlayerStatsData> getResponse() {
        return response;
    }

    public void setResponse(List<PlayerStatsData> response) {
        this.response = response;
    }
}

