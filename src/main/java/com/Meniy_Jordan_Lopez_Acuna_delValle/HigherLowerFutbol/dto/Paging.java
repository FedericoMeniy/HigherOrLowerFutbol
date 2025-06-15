package com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.dto;

import lombok.Data;

@Data
public class Paging {
    private Integer current; // La página actual de la respuesta
    private Integer total;

    public Integer getCurrent() {
        return current;
    }

    public void setCurrent(Integer current) {
        this.current = current;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }
}
