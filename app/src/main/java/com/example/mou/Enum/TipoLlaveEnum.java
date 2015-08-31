package com.example.mou.Enum;

/**
 * Created by Mou on 05/08/2015.
 */
public enum TipoLlaveEnum {

    LLAVE_VEHICULO(1),
    LLAVE_INVITACION(2);

    private int id;

    TipoLlaveEnum(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
