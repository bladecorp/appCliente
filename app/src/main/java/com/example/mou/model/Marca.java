package com.example.mou.model;

/**
 * Created by Mou on 04/07/2015.
 */
public class Marca {

    private Integer idMarca;
    private String nombre;

    public Integer getIdMarca() {
        return idMarca;
    }

    public void setIdMarca(Integer idMarca) {
        this.idMarca = idMarca;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String toString(){
        return this.nombre;
    }
}
