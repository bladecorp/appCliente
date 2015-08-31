package com.example.mou.Enum;

/**
 * Created by Mou on 10/07/2015.
 */
public enum TipoUsuarioEnum {
    CLIENTE(0),
    TITULAR(1);

    private int id;

    TipoUsuarioEnum(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
