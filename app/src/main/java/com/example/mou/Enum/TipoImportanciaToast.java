package com.example.mou.Enum;

/**
 * Created by Mou on 07/07/2015.
 */
public enum TipoImportanciaToast {

    INFO(1),
    ERROR(2);

    private int id;

    TipoImportanciaToast(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
