package com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FutbolistaDTO {

    @JsonProperty("id")
    private Integer idApi;

    @JsonProperty("name")
    private String nombre;

    @JsonProperty("photo")
    private String imagen;

    private int goles;
    private int asistencias;
    private int tarjetasRojas;
    private int tarjetasAmarillas;

    public Integer getIdApi() {
        return idApi;
    }

    public void setIdApi(Integer idApi) {
        this.idApi = idApi;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public int getGoles() {
        return goles;
    }

    public void setGoles(int goles) {
        this.goles = goles;
    }

    public int getAsistencias() {
        return asistencias;
    }

    public void setAsistencias(int asistencias) {
        this.asistencias = asistencias;
    }

    public int getTarjetasRojas() {
        return tarjetasRojas;
    }

    public void setTarjetasRojas(int tarjetasRojas) {
        this.tarjetasRojas = tarjetasRojas;
    }

    public int getTarjetasAmarillas() {
        return tarjetasAmarillas;
    }

    public void setTarjetasAmarillas(int tarjetasAmarillas) {
        this.tarjetasAmarillas = tarjetasAmarillas;
    }
}
