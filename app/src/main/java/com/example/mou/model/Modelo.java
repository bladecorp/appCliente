package com.example.mou.model;

/**
 * Created by Mou on 04/07/2015.
 */
public class Modelo {

    private Integer idModelo;
    private String nombre;
    private Integer idMarca;

    public Integer getIdModelo() {
        return idModelo;
    }

    public void setIdModelo(Integer idModelo) {
        this.idModelo = idModelo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getIdMarca() {
        return idMarca;
    }

    public void setIdMarca(Integer idMarca) {
        this.idMarca = idMarca;
    }

    public String toString(){
        return this.nombre;
    }
}
