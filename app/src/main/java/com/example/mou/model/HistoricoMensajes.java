package com.example.mou.model;

import java.util.Date;

/**
 * Created by Mou on 05/08/2015.
 */
public class HistoricoMensajes {

    private Integer id;
    private String mensaje;
    private Integer idTipoMensaje;
    private Integer idSubMensaje;
    private String fecha;
    private Integer idVehiculo;
    private Integer tipoUsuario;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public Integer getIdTipoMensaje() {
        return idTipoMensaje;
    }

    public void setIdTipoMensaje(Integer idTipoMensaje) {
        this.idTipoMensaje = idTipoMensaje;
    }

    public Integer getIdVehiculo() {
        return idVehiculo;
    }

    public void setIdVehiculo(Integer idVehiculo) {
        this.idVehiculo = idVehiculo;
    }

    public Integer getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(Integer tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public Integer getIdSubMensaje() {
        return idSubMensaje;
    }

    public void setIdSubMensaje(Integer idSubMensaje) {
        this.idSubMensaje = idSubMensaje;
    }
}
