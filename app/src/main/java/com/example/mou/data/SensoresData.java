package com.example.mou.data;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.mou.model.Sensor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mou on 22/08/2015.
 */
public class SensoresData {

    private static final String CLMN_ID = "id";
    private static final String CLMN_SENSOR = "sensor";

    public static final String TABLE_NAME = "sensores";

    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " ("
            +CLMN_ID+" INTEGER PRIMARY KEY, "
            +CLMN_SENSOR+" TEXT"
            +");";
    public static final String INSERT_SENSORES = "INSERT INTO "+TABLE_NAME+" ("+CLMN_ID+","+CLMN_SENSOR+") VALUES "
            +"('1', 'Proximidad'),"+"('2', 'Movimiento'),"+"('3','Carga');";

    private SQLiteDatabase db;

    public SensoresData(BaseDatos miBase) {
        db = miBase.getWritableDatabase();
    }

    public List<Sensor> obtenerTiposDeLlave(){
        List<Sensor> sensores = new ArrayList<>();
        Cursor c = db.rawQuery("SELECT * FROM "+TABLE_NAME, null);
        while(c.moveToNext()){
            Sensor sensor = new Sensor();
            sensor.setId(c.getInt(c.getColumnIndex(CLMN_ID)));
            sensor.setNombre(c.getString(c.getColumnIndex(CLMN_SENSOR)));
            sensores.add(sensor);
        }
        return sensores;
    }

    public Sensor obtenerSensorPorId(Integer id){
        if(id != null){
            Cursor c = db.rawQuery("SELECT * FROM "+TABLE_NAME+" WHERE "+CLMN_ID+" =?", new String[]{id.toString()});
            if(c.moveToFirst()){
                Sensor sensor = new Sensor();
                sensor.setId(c.getInt(c.getColumnIndex(CLMN_ID)));
                sensor.setNombre(c.getString(c.getColumnIndex(CLMN_SENSOR)));
                return sensor;
            }
        }
        return null;
    }

}
