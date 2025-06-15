package com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Data

public class JugadoresApiResponse {
    private List<PlayerStatsData> response;
    private Paging paging;

    public List<PlayerStatsData> getResponse() {
        return response;
    }

    public void setResponse(List<PlayerStatsData> response) {
        this.response = response;
    }

    public Paging getPaging() {
        return paging;
    }

    public void setPaging(Paging paging) {
        this.paging = paging;
    }
}

