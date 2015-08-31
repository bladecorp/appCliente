package com.example.mou.data;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.mou.model.TipoLlave;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mou on 05/08/2015.
 */
public class TipoLlaveData {

    private static final String CLMN_ID = "id";
    private static final String CLMN_TIPO = "tipo";

    public static final String TABLE_NAME = "tipollave";

    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " ("
            +CLMN_ID+" INTEGER PRIMARY KEY, "
            +CLMN_TIPO+" TEXT"
            +");";
    public static final String INSERT_TIPOLLAVE = "INSERT INTO "+TABLE_NAME+" ("+CLMN_ID+","+CLMN_TIPO+") VALUES "
            +"('1', 'Llave de Vehículo'),"+"('2','Llave de Invitación');";

    private SQLiteDatabase db;

    public TipoLlaveData(BaseDatos miBase) {
        db = miBase.getWritableDatabase();
    }

    public List<TipoLlave> obtenerTiposDeLlave(){
        List<TipoLlave> tiposLlave = new ArrayList<>();
        Cursor c = db.rawQuery("SELECT * FROM "+TABLE_NAME, null);
        while(c.moveToNext()){
            TipoLlave tipo = new TipoLlave();
            tipo.setId(c.getInt(c.getColumnIndex(CLMN_ID)));
            tipo.setTipo(c.getString(c.getColumnIndex(CLMN_TIPO)));
            tiposLlave.add(tipo);
        }
        return tiposLlave;
    }

    public TipoLlave obtenerTipoLlavePorId(Integer id){
        if(id != null){
            Cursor c = db.rawQuery("SELECT * FROM "+TABLE_NAME+" WHERE "+CLMN_ID+" =?", new String[]{id.toString()});
            if(c.moveToFirst()){
                TipoLlave tipo = new TipoLlave();
                tipo.setId(c.getInt(c.getColumnIndex(CLMN_ID)));
                tipo.setTipo(c.getString(c.getColumnIndex(CLMN_TIPO)));
                return tipo;
            }
        }
        return null;
    }

}
