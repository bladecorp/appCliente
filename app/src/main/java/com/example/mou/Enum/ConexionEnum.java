package com.example.mou.Enum;

/**
 * Created by Mou on 13/08/2015.
 */
public enum ConexionEnum {

    SIN_CONEXION(0),
    CON_CONEXION(1);

    private int id;

    ConexionEnum(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

}
