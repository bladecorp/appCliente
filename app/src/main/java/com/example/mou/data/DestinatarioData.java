package com.example.mou.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.mou.model.Destinatario;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mou on 05/08/2015.
 */
public class DestinatarioData {

    private static final String CLMN_ID = "id";
    private static final String CLMN_NOMBRE = "tipo";
    private static final String CLMN_APATERNO = "apaterno";
    private static final String CLMN_AMATERNO = "amaterno";
    private static final String CLMN_TELEFONO = "telefono";
    private static final String CLMN_STATUS_INV = "statinv";
    private static final String CLMN_IDVEHICULO = "idvehiculo";

    public static final String TABLE_NAME = "destinatarios";

    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " ("
            +CLMN_ID+" INTEGER PRIMARY KEY, "
            +CLMN_NOMBRE+" TEXT, "
            +CLMN_APATERNO+" TEXT, "
            +CLMN_AMATERNO+" TEXT, "
            +CLMN_TELEFONO+" TEXT, "
            +CLMN_STATUS_INV+" TEXT, "
            +CLMN_IDVEHICULO+" INTEGER"
            +");";

    private SQLiteDatabase db;

    public DestinatarioData(BaseDatos miBase) {
        db = miBase.getWritableDatabase();
    }

    public Destinatario obtenerDestinatarioPorId(Integer id){
        if(id != null){
            Cursor c = db.rawQuery("SELECT * FROM "+TABLE_NAME+" WHERE "+CLMN_ID+" =?", new String[]{id.toString()});
            if(c.moveToFirst()){
                Destinatario dest = new Destinatario();
                dest.setId(c.getInt(c.getColumnIndex(CLMN_ID)));
                dest.setNombre(c.getString(c.getColumnIndex(CLMN_NOMBRE)));
                dest.setaPaterno(c.getString(c.getColumnIndex(CLMN_APATERNO)));
                dest.setaMaterno(c.getString(c.getColumnIndex(CLMN_AMATERNO)));
                dest.setTelefono(c.getString(c.getColumnIndex(CLMN_TELEFONO)));
                dest.setStatusInv(c.getInt(c.getColumnIndex(CLMN_STATUS_INV)));
                dest.setIdVehiculo(c.getInt(c.getColumnIndex(CLMN_IDVEHICULO)));
                return dest;
            }
        }
        return null;
    }

    public List<Destinatario> obtenerDestinatariosPorIdVehiculo(Integer idVehiculo){
        List<Destinatario> destinatarios = new ArrayList<>();
        if(idVehiculo!=null){
            Cursor c = db.rawQuery("SELECT * FROM "+TABLE_NAME+" WHERE "+CLMN_IDVEHICULO+" =?", new String[]{idVehiculo.toString()});
            while (c.moveToNext()){
                Destinatario dest = new Destinatario();
                dest.setId(c.getInt(c.getColumnIndex(CLMN_ID)));
                dest.setNombre(c.getString(c.getColumnIndex(CLMN_NOMBRE)));
                dest.setaPaterno(c.getString(c.getColumnIndex(CLMN_APATERNO)));
                dest.setaMaterno(c.getString(c.getColumnIndex(CLMN_AMATERNO)));
                dest.setTelefono(c.getString(c.getColumnIndex(CLMN_TELEFONO)));
                dest.setStatusInv(c.getInt(c.getColumnIndex(CLMN_STATUS_INV)));
                dest.setIdVehiculo(c.getInt(c.getColumnIndex(CLMN_IDVEHICULO)));
                destinatarios.add(dest);
            }
        }
        return destinatarios;
    }

    public void eliminarTodo(){
        db.delete(TABLE_NAME,null, null);
    }

    public void insertarDestinatario(Destinatario destinatario){
        if(destinatario != null){
            ContentValues cv = new ContentValues();
            cv.put(CLMN_ID,destinatario.getId());
            cv.put(CLMN_IDVEHICULO,destinatario.getIdVehiculo());
            cv.put(CLMN_NOMBRE,destinatario.getNombre());
            cv.put(CLMN_TELEFONO,destinatario.getTelefono());
            cv.put(CLMN_APATERNO,destinatario.getaPaterno());
            cv.put(CLMN_AMATERNO,destinatario.getaMaterno());
            cv.put(CLMN_STATUS_INV, destinatario.getStatusInv());
            db.insert(TABLE_NAME,null,cv);
        }

    }

    public int eliminarDestinatarioPorIdVehiculo(Integer idVehiculo){
       return db.delete(TABLE_NAME,CLMN_IDVEHICULO+"=?",new String[]{idVehiculo.toString()});
    }

    public boolean actualizarStatusInvitacion(Integer idDestinatario, Integer statusInvitacion){
        if(idDestinatario != null && statusInvitacion != null){
            ContentValues cv = new ContentValues();
            cv.put(CLMN_STATUS_INV, statusInvitacion);
            int exito = db.update(TABLE_NAME,cv,CLMN_ID+"=?",new String[]{idDestinatario.toString()});
            return exito==1?true:false;
        }
        return false;
    }

}
