package com.example.mou.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.mou.Enum.StatusInvitacion;
import com.example.mou.model.Invitacion;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mou on 21/08/2015.
 */
public class InvitacionesData {

    private static final String CLMN_ID = "id";
    private static final String CLMN_IDDESTINATARIO = "idDestinatario";
    private static final String CLMN_IDMODELO = "idModelo";
    private static final String CLMN_IDVEHICULO = "idVehiculo";
    private static final String CLMN_ID_STATUS = "idStatus";

    public static final String TABLE_NAME = "invitaciones";

    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " ("
            +CLMN_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "
            +CLMN_IDDESTINATARIO+" INTEGER, "
            +CLMN_IDMODELO+" INTEGER, "
            +CLMN_IDVEHICULO+" INTEGER, "
            +CLMN_ID_STATUS+" INTEGER"
            +");";

    private SQLiteDatabase db;

    public InvitacionesData(BaseDatos miBase) {
        db = miBase.getWritableDatabase();
    }

    public List<Invitacion> obtenerInvitacionesPendientes(){
        List<Invitacion> invitaciones = new ArrayList<>();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_NAME+" WHERE "+CLMN_ID_STATUS+"=?", new String[]{String.valueOf(StatusInvitacion.RECIBIDA.getId())});
        while (c.moveToNext()) {
            Invitacion invitacion = new Invitacion();
            invitacion.setId(c.getInt(c.getColumnIndex(CLMN_ID)));
            invitacion.setIdDestinatario(c.getInt(c.getColumnIndex(CLMN_IDDESTINATARIO)));
            invitacion.setIdModelo(c.getInt(c.getColumnIndex(CLMN_IDMODELO)));
            invitacion.setIdVehiculo(c.getInt(c.getColumnIndex(CLMN_IDVEHICULO)));
            invitacion.setIdStatus(c.getInt(c.getColumnIndex(CLMN_ID_STATUS)));
            invitaciones.add(invitacion);
        }

        return invitaciones;
    }

    public void insertarInvitacion(Invitacion invitacion){
        if(invitacion != null){
            ContentValues cv = new ContentValues();
            cv.put(CLMN_IDDESTINATARIO, invitacion.getIdDestinatario());
            cv.put(CLMN_IDMODELO, invitacion.getIdModelo());
            cv.put(CLMN_IDVEHICULO, invitacion.getIdVehiculo());
            cv.put(CLMN_ID_STATUS, invitacion.getIdStatus());
            db.insert(TABLE_NAME, null, cv);
        }
    }

    public int actualizarInvitacion(Integer idInvitacion, Integer idStatusInvitacion){
        if(idInvitacion!=null){
        ContentValues cv = new ContentValues();
        cv.put(CLMN_ID_STATUS, idStatusInvitacion);
        int exito = db.update(TABLE_NAME, cv, CLMN_ID+"=?",new String[]{idInvitacion.toString()});
        return exito;
        }
        return -1;
    }


    public void eliminarTodo(){
        db.delete(TABLE_NAME,null, null);
    }

    public void eliminarInvitacionPorId(Integer idInvitacion){
        if(idInvitacion!=null){
            db.delete(TABLE_NAME,CLMN_ID+"=?",new String[]{idInvitacion.toString()});
        }
    }

}
