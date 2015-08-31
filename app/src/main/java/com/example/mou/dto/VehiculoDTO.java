package com.example.mou.dto;

import com.example.mou.model.Destinatario;
import com.example.mou.model.Llave;
import com.example.mou.model.Marca;
import com.example.mou.model.Modelo;
import com.example.mou.model.TipoStatus;
import com.example.mou.model.TipoVehiculo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mou on 07/08/2015.
 */
public class VehiculoDTO {

    private Integer id;
    private Marca marca;
    private Modelo modelo;
    private String placas;
    private String telefono;
    private TipoVehiculo tipo;
    private TipoStatus status;
    private List<Llave> llaves;
    private List<Destinatario> destinatarios;

    public VehiculoDTO(){
        marca = new Marca();
        modelo = new Modelo();
        tipo = new TipoVehiculo();
        status = new TipoStatus();
        llaves = new ArrayList<Llave>();
        destinatarios = new ArrayList<>();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Marca getMarca() {
        return marca;
    }

    public void setMarca(Marca marca) {
        this.marca = marca;
    }

    public Modelo getModelo() {
        return modelo;
    }

    public void setModelo(Modelo modelo) {
        this.modelo = modelo;
    }

    public String getPlacas() {
        return placas;
    }

    public void setPlacas(String placas) {
        this.placas = placas;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public TipoVehiculo getTipo() {
        return tipo;
    }

    public void setTipo(TipoVehiculo tipo) {
        this.tipo = tipo;
    }

    public TipoStatus getStatus() {
        return status;
    }

    public void setStatus(TipoStatus status) {
        this.status = status;
    }

    public List<Llave> getLlaves() {
        return llaves;
    }

    public void setLlaves(List<Llave> llaves) {
        this.llaves = llaves;
    }

    public List<Destinatario> getDestinatarios() {
        return destinatarios;
    }

    public void setDestinatarios(List<Destinatario> destinatarios) {
        this.destinatarios = destinatarios;
    }
}
