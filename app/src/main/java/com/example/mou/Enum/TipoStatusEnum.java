package com.example.mou.Enum;

/**
 * Created by Mou on 10/07/2015.
 */
public enum TipoStatusEnum {

    ACTIVADO(1),
    DESACTIVADO(2);

    private int id;

    TipoStatusEnum(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

}
