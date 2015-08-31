package com.example.mou.model;

/**
 * Created by Mou on 05/08/2015.
 */
public class Destinatario {

    private Integer id;
    private String nombre;
    private String aPaterno;
    private String aMaterno;
    private String telefono;
    private Integer statusInv;
    private Integer idVehiculo;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getaPaterno() {
        return aPaterno;
    }

    public void setaPaterno(String aPaterno) {
        this.aPaterno = aPaterno;
    }

    public String getaMaterno() {
        return aMaterno;
    }

    public void setaMaterno(String aMaterno) {
        this.aMaterno = aMaterno;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public Integer getIdVehiculo() {
        return idVehiculo;
    }

    public void setIdVehiculo(Integer idVehiculo) {
        this.idVehiculo = idVehiculo;
    }

    public Integer getStatusInv() {
        return statusInv;
    }

    public void setStatusInv(Integer statusInv) {
        this.statusInv = statusInv;
    }
}
