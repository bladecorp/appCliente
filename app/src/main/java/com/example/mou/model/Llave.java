package com.example.mou.model;

/**
 * Created by Mou on 05/08/2015.
 */
public class Llave {

    private String llave;
    private Integer idTipoLlave;
    private Integer idVehiculo;
    private boolean titular;

    public String getLlave() {
        return llave;
    }

    public void setLlave(String llave) {
        this.llave = llave;
    }

    public Integer getIdTipoLlave() {
        return idTipoLlave;
    }

    public void setIdTipoLlave(Integer idTipoLlave) {
        this.idTipoLlave = idTipoLlave;
    }

    public Integer getIdVehiculo() {
        return idVehiculo;
    }

    public void setIdVehiculo(Integer idVehiculo) {
        this.idVehiculo = idVehiculo;
    }

    public boolean isTitular() {
        return titular;
    }

    public void setTitular(boolean titular) {
        this.titular = titular;
    }
}
