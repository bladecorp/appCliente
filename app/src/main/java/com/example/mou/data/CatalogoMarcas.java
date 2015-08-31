package com.example.mou.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.example.mou.model.Marca;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mou on 04/07/2015.
 */
public class CatalogoMarcas {

    private static final String CLMN_ID = "idMarca";
    private static final String CLMN_NOMBRE = "nombre";

    public static final String TABLE_NAME = "marcas";

    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " ("
            +CLMN_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "
            +CLMN_NOMBRE+" TEXT "
            +");";
    public static final String INSERT_MARCAS = "INSERT INTO "+TABLE_NAME+" ("+CLMN_NOMBRE+") VALUES "
            +"('ACURA'),"+"('AUDI'),"+"('BMW'),"+"('CADILLAC'),"+"('CHEVROLET'),"
            +"('CHRYSLER'),"+"('DODGE'),"+"('FIAT'),"+"('FORD'),"+"('GMC'),"
            +"('HONDA'),"+"('JEEP'),"+"('KIA'),"+"('MAZDA'),"+"('MERCEDEZ-BENZ'),"
            +"('NISSAN'),"+"('PEUGEOT'),"+"('RENAULT'),"+"('SEAT'),"+"('TOYOTA'),"
            +"('VOLKSWAGEN'),"+"('VOLVO')"+";";

    private SQLiteDatabase db;

    public CatalogoMarcas(BaseDatos miBase) {
        db = miBase.getWritableDatabase();
    }

    public List<Marca> obtenerMarcas(){
        List<Marca> marcas = new ArrayList<Marca>();
        Cursor cursor = db.rawQuery("SELECT * FROM "+TABLE_NAME+" ORDER BY "+CLMN_NOMBRE, null);
        while(cursor.moveToNext()){
            Marca marca = new Marca();
            marca.setIdMarca(cursor.getInt(cursor.getColumnIndex(CLMN_ID)));
            marca.setNombre(cursor.getString(cursor.getColumnIndex(CLMN_NOMBRE)));
            marcas.add(marca);
        }
        cursor.close();
        return marcas;
    }

    public Marca obtenerMarcaPorId(Integer id){
        if(id!=null) {
            Marca marca = new Marca();
            Cursor c = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + CLMN_ID + "=?", new String[]{id.toString()});
            if(c.moveToFirst()){
                marca.setIdMarca(c.getInt(c.getColumnIndex(CLMN_ID)));
                marca.setNombre(c.getString(c.getColumnIndex(CLMN_NOMBRE)));
                c.close();
                return marca;
            }
            c.close();
        }
        return null;
    }

}
