package com.example.mou.data;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.mou.model.TipoVehiculo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mou on 05/08/2015.
 */
public class TipoVehiculoData {

    private static final String CLMN_ID = "id";
    private static final String CLMN_TIPO = "tipo";

    public static final String TABLE_NAME = "tipovehiculo";

    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " ("
            +CLMN_ID+" INTEGER PRIMARY KEY, "
            +CLMN_TIPO+" TEXT"
            +");";
    public static final String INSERT_TIPOVEHICULO = "INSERT INTO "+TABLE_NAME+" ("+CLMN_ID+","+CLMN_TIPO+") VALUES "
            +"('1', 'Automóvil'),"+"('2','Camioneta');";

    private SQLiteDatabase db;

    public TipoVehiculoData(BaseDatos miBase) {
        db = miBase.getWritableDatabase();
    }

    public List<TipoVehiculo> obtenerTiposDeVehiculo(){
        List<TipoVehiculo> tiposVehiculo = new ArrayList<>();
        Cursor c = db.rawQuery("SELECT * FROM "+TABLE_NAME, null);
        while(c.moveToNext()){
            TipoVehiculo tipo = new TipoVehiculo();
            tipo.setId(c.getInt(c.getColumnIndex(CLMN_ID)));
            tipo.setTipo(c.getString(c.getColumnIndex(CLMN_TIPO)));
            tiposVehiculo.add(tipo);
        }
        return tiposVehiculo;
    }

    public TipoVehiculo obtenerTipoVehiculoPorId(Integer id){
        if(id != null){
            Cursor c = db.rawQuery("SELECT * FROM "+TABLE_NAME+" WHERE "+CLMN_ID+" =?", new String[]{id.toString()});
            if(c.moveToFirst()){
                TipoVehiculo tipo = new TipoVehiculo();
                tipo.setId(c.getInt(c.getColumnIndex(CLMN_ID)));
                tipo.setTipo(c.getString(c.getColumnIndex(CLMN_TIPO)));
                return tipo;
            }
        }
        return null;
    }

}
