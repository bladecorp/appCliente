package com.example.mou.data;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.mou.model.TipoStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mou on 05/08/2015.
 */
public class TipoStatusData {

    private static final String CLMN_ID = "id";
    private static final String CLMN_TIPO = "tipo";

    public static final String TABLE_NAME = "tipostatus";

    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " ("
            +CLMN_ID+" INTEGER PRIMARY KEY, "
            +CLMN_TIPO+" TEXT"
            +");";
    public static final String INSERT_TIPOSTATUS = "INSERT INTO "+TABLE_NAME+" ("+CLMN_ID+","+CLMN_TIPO+") VALUES "
            +"('1', 'Activado'),"+"('2','Desactivado');";

    private SQLiteDatabase db;

    public TipoStatusData(BaseDatos miBase) {
        db = miBase.getWritableDatabase();
    }

    public List<TipoStatus> obtenerTiposDeStatus(){
        List<TipoStatus> tiposStatus = new ArrayList<>();
        Cursor c = db.rawQuery("SELECT * FROM "+TABLE_NAME, null);
        while(c.moveToNext()){
            TipoStatus tipo = new TipoStatus();
            tipo.setId(c.getInt(c.getColumnIndex(CLMN_ID)));
            tipo.setTipo(c.getString(c.getColumnIndex(CLMN_TIPO)));
            tiposStatus.add(tipo);
        }
        return tiposStatus;
    }

    public TipoStatus obtenerTipoStatusPorId(Integer id){
        if(id != null){
            Cursor c = db.rawQuery("SELECT * FROM "+TABLE_NAME+" WHERE "+CLMN_ID+" =?", new String[]{id.toString()});
            if(c.moveToFirst()){
                TipoStatus tipo = new TipoStatus();
                tipo.setId(c.getInt(c.getColumnIndex(CLMN_ID)));
                tipo.setTipo(c.getString(c.getColumnIndex(CLMN_TIPO)));
                return tipo;
            }
        }
        return null;
    }

}
