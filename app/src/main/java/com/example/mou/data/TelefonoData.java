package com.example.mou.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.mou.model.Telefono;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mou on 05/08/2015.
 */
public class TelefonoData {

    private static final String CLMN_ID = "id";
    private static final String CLMN_TELEFONO = "telefono";
    private static final String CLMN_IDUSUARIO = "idUsuario";

    public static final String TABLE_NAME = "telefonos";

    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " ("
            +CLMN_ID+" INTEGER PRIMARY KEY, "
            +CLMN_TELEFONO+" TEXT, "
            +CLMN_IDUSUARIO+" INTEGER"
            +");";

    private SQLiteDatabase db;

    public TelefonoData(BaseDatos miBase) {
        db = miBase.getWritableDatabase();
    }

    public Telefono obtenerTelefonoPorId(Integer id){
        if(id != null){
            Cursor c = db.rawQuery("SELECT * FROM "+TABLE_NAME+" WHERE "+CLMN_ID+" =?", new String[]{id.toString()});
            if(c.moveToFirst()){
                Telefono telefono  = new Telefono();
                telefono.setId(c.getInt(c.getColumnIndex(CLMN_ID)));
                telefono.setTelefono(c.getString(c.getColumnIndex(CLMN_TELEFONO)));
                telefono.setIdUsuario(c.getInt(c.getColumnIndex(CLMN_IDUSUARIO)));
                return telefono;
            }
        }
        return null;
    }

    public List<Telefono> obtenerTelefonosPorIdUsuario(Integer idUsuario){
        List<Telefono> telefonos = new ArrayList<>();
        if(idUsuario!=null) {
            Cursor c = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + CLMN_IDUSUARIO + " =?", new String[]{idUsuario.toString()});
            while(c.moveToNext()){
                Telefono telefono = new Telefono();
                telefono.setId(c.getInt(c.getColumnIndex(CLMN_ID)));
                telefono.setTelefono(c.getString(c.getColumnIndex(CLMN_TELEFONO)));
                telefono.setIdUsuario(c.getInt(c.getColumnIndex(CLMN_IDUSUARIO)));
                telefonos.add(telefono);
            }
        }
        return telefonos;
    }

    public boolean insertarTelefono(Telefono telefono){
        if(telefono != null){
            ContentValues cv = new ContentValues();
            cv.put(CLMN_ID, telefono.getId());
            cv.put(CLMN_TELEFONO, telefono.getTelefono());
            cv.put(CLMN_IDUSUARIO, telefono.getIdUsuario());
            long exito = db.insert(TABLE_NAME, null, cv);
            if(exito != -1){
                return true;
            }
        }
        return false;
    }

    public void insertarTelefonos(List<Telefono> telefonos){
        if(telefonos!=null){
            for(Telefono telefono : telefonos){
                ContentValues cv = new ContentValues();
                cv.put(CLMN_ID, telefono.getId());
                cv.put(CLMN_TELEFONO, telefono.getTelefono());
                cv.put(CLMN_IDUSUARIO, telefono.getIdUsuario());
                db.insert(TABLE_NAME, null, cv);
            }
        }
    }

    public void eliminarTodo(){
        db.delete(TABLE_NAME,null, null);
    }

}
