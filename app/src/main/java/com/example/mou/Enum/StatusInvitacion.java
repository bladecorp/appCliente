package com.example.mou.Enum;

/**
 * Created by Mou on 19/08/2015.
 */
public enum StatusInvitacion {

    ENVIADA(1),
    NO_ENVIADA(2),
    ACEPTADA(3),
    RECHAZADA(4),
    RECIBIDA(5);

    private int id;

    private StatusInvitacion(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

}
