package com.example.mou.dto;

import com.example.mou.model.Telefono;
import com.example.mou.model.Usuario;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mou on 08/08/2015.
 */
public class UsuarioDTO {

    private Usuario usuario;
    private List<Telefono> telefonos;

    public UsuarioDTO(){
        usuario = new Usuario();
        telefonos = new ArrayList<>();
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public List<Telefono> getTelefonos() {
        return telefonos;
    }

    public void setTelefonos(List<Telefono> telefonos) {
        this.telefonos = telefonos;
    }
}
