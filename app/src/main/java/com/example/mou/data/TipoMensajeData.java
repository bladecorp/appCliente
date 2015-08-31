package com.example.mou.data;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.mou.model.TipoMensaje;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mou on 05/08/2015.
 */
public class TipoMensajeData {

    private static final String CLMN_ID = "id";
    private static final String CLMN_TIPO = "tipo";

    public static final String TABLE_NAME = "tipomensaje";

    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " ("
            +CLMN_ID+" INTEGER PRIMARY KEY, "
            +CLMN_TIPO+" TEXT"
            +");";
    public static final String INSERT_TIPOMENSAJE = "INSERT INTO "+TABLE_NAME+" ("+CLMN_ID+","+CLMN_TIPO+") VALUES "
            +"('1', ''),"+"('2','');";

    private SQLiteDatabase db;

    public TipoMensajeData(BaseDatos miBase) {
        db = miBase.getWritableDatabase();
    }

    public List<TipoMensaje> obtenerTiposDeMensaje(){
        List<TipoMensaje> tiposMensaje = new ArrayList<>();
        Cursor c = db.rawQuery("SELECT * FROM "+TABLE_NAME, null);
        while(c.moveToNext()){
            TipoMensaje tipo = new TipoMensaje();
            tipo.setId(c.getInt(c.getColumnIndex(CLMN_ID)));
            tipo.setTipo(c.getString(c.getColumnIndex(CLMN_TIPO)));
            tiposMensaje.add(tipo);
        }
        return tiposMensaje;
    }

    public TipoMensaje obtenerTipoMensajePorId(Integer idTipoMensaje){
        if(idTipoMensaje != null) {
            Cursor c = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + CLMN_ID + "=?", new String[]{idTipoMensaje.toString()});
            if(c.moveToFirst()){
                TipoMensaje tipoMensaje = new TipoMensaje();
                tipoMensaje.setId(c.getInt(c.getColumnIndex(CLMN_ID)));
                tipoMensaje.setTipo(c.getString(c.getColumnIndex(CLMN_TIPO)));
                return tipoMensaje;
            }
        }
        return null;
    }

}
