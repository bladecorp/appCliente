package com.example.mou.Enum;

/**
 * Created by Mou on 13/08/2015.
 */
public enum ComunicacionEnum {
    SIN_COMUNICACION(0),
    CON_COMUNICACION(1);

    private int id;

    ComunicacionEnum(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

}

