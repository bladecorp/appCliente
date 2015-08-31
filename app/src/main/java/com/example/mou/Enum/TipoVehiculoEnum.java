package com.example.mou.Enum;

/**
 * Created by Mou on 07/07/2015.
 */
public enum TipoVehiculoEnum {

    AUTO(1),
    CAMIONETA(2);

    private int id;

    TipoVehiculoEnum(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
