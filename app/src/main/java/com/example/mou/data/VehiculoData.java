package com.example.mou.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.mou.Enum.TipoStatusEnum;
import com.example.mou.Enum.TipoUsuarioEnum;
import com.example.mou.model.TipoUsuario;
import com.example.mou.model.Vehiculo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mou on 17/01/2015.
 */
public class VehiculoData {

    private static final String CLMN_ID_VEHICULO = "id";
    private static final String CLMN_ID_MODELO = "modelo";
    private static final String CLMN_PLACAS = "placas";
    private static final String CLMN_TELEFONO = "telefono";
    private static final String CLMN_TIPO = "tipo";
    private static final String CLMN_STATUS = "status";
    private static final String CLMN_ID_USUARIO = "tipoUsuario";
    private static final String CLMN_ES_TITULAR = "titular";

    public static final String TABLE_NAME = "vehiculos";

    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " ("
         +CLMN_ID_VEHICULO+" INTEGER, "
         +CLMN_ID_MODELO+" INTEGER, "
         +CLMN_PLACAS+" TEXT, "
         +CLMN_TELEFONO+" TEXT, "
         +CLMN_TIPO+" INTEGER, "
         +CLMN_STATUS+" INTEGER,"
         +CLMN_ID_USUARIO +" INTEGER, "
         +CLMN_ES_TITULAR +" INTEGER"
         +");";

    private SQLiteDatabase db;


    public VehiculoData(BaseDatos miBase) {
        db = miBase.getWritableDatabase();
    }

    public Integer insertarRegistro(Vehiculo vehiculo){
        ContentValues cv = new ContentValues();
        cv.put(CLMN_ID_VEHICULO, vehiculo.getIdVehiculo());
        cv.put(CLMN_ID_MODELO, vehiculo.getIdModelo());
        cv.put(CLMN_PLACAS, vehiculo.getPlacas());
        cv.put(CLMN_TELEFONO, vehiculo.getTelefono());
        cv.put(CLMN_TIPO, vehiculo.getTipo());
        cv.put(CLMN_STATUS, vehiculo.getStatus());
        cv.put(CLMN_ID_USUARIO, vehiculo.getIdUsuario());
        cv.put(CLMN_ES_TITULAR, vehiculo.isTitular() ? 1 : 0);
        long res = db.insert(TABLE_NAME, null, cv);
        return (int) res;
    }

    public List<Vehiculo> obtenerTodosVehiculos(){
        List<Vehiculo> vehiculos = new ArrayList<>();
        Cursor c = db.rawQuery("SELECT * FROM "+TABLE_NAME, null);
        while(c.moveToNext()){
            Vehiculo vehiculo = new Vehiculo();
            vehiculo.setIdVehiculo(c.getInt(c.getColumnIndex(CLMN_ID_VEHICULO)));
            vehiculo.setIdModelo(c.getInt(c.getColumnIndex(CLMN_ID_MODELO)));
            vehiculo.setPlacas(c.getString(c.getColumnIndex(CLMN_PLACAS)));
            vehiculo.setTelefono(c.getString(c.getColumnIndex(CLMN_TELEFONO)));
            vehiculo.setTipo(c.getInt(c.getColumnIndex(CLMN_TIPO)));
            vehiculo.setStatus(c.getInt(c.getColumnIndex(CLMN_STATUS)));
            vehiculo.setIdUsuario(c.getInt(c.getColumnIndex(CLMN_ID_USUARIO)));
            vehiculo.setTitular(c.getInt(c.getColumnIndex(CLMN_ES_TITULAR)) == 1);
            vehiculos.add(vehiculo);
        }
        c.close();
        return vehiculos;
    }

    public Integer borrarVehiculoPorId(Integer id){
        return db.delete(TABLE_NAME, CLMN_ID_VEHICULO +"=?", new String[]{id.toString()});
    }

    public Integer borrarPorTipoUsuario(Integer tipoUsuario){
        return db.delete(TABLE_NAME, CLMN_ES_TITULAR+" =?", new String[]{tipoUsuario.toString()});
    }

    public Integer borrarTodos(){
        return (int)db.delete(TABLE_NAME, null, null);
    }

    public Vehiculo obtenerVehiculoPorId(Integer id){
        Cursor c = db.rawQuery("SELECT * FROM "+TABLE_NAME+" WHERE "+ CLMN_ID_VEHICULO +"=?",new String[]{id.toString()});
        if(c.moveToFirst()){
            Vehiculo vehiculo = new Vehiculo();
            vehiculo.setIdVehiculo(c.getInt(c.getColumnIndex(CLMN_ID_VEHICULO)));
            vehiculo.setIdModelo(c.getInt(c.getColumnIndex(CLMN_ID_MODELO)));
            vehiculo.setPlacas(c.getString(c.getColumnIndex(CLMN_PLACAS)));
            vehiculo.setTelefono(c.getString(c.getColumnIndex(CLMN_TELEFONO)));
            vehiculo.setTipo(c.getInt(c.getColumnIndex(CLMN_TIPO)));
            vehiculo.setStatus(c.getInt(c.getColumnIndex(CLMN_STATUS)));
            vehiculo.setIdUsuario(c.getInt(c.getColumnIndex(CLMN_ID_USUARIO)));
            vehiculo.setTitular(c.getInt(c.getColumnIndex(CLMN_ES_TITULAR)) == 1);
            c.close();
            return vehiculo;
        }
        c.close();
        return null;
    }

    public Integer actualizarVehiculo(Vehiculo vehiculo){
        ContentValues cv = new ContentValues();
        cv.put(CLMN_ID_MODELO, vehiculo.getIdModelo());
        cv.put(CLMN_PLACAS, vehiculo.getPlacas());
        cv.put(CLMN_TELEFONO, vehiculo.getTelefono());
        cv.put(CLMN_TIPO, vehiculo.getTipo());
        cv.put(CLMN_STATUS, TipoStatusEnum.DESACTIVADO.getId());
        cv.put(CLMN_ID_USUARIO, vehiculo.getIdUsuario());
        cv.put(CLMN_ES_TITULAR, vehiculo.isTitular() ?1:0);
        return db.update(TABLE_NAME,cv,CLMN_ID_VEHICULO+"=?", new String[]{vehiculo.getIdVehiculo().toString()});
    }

    public Integer cambiarStatusPorIdVehiculo(Integer idVehiculo, Integer idStatus){
        ContentValues cv = new ContentValues();
        cv.put(CLMN_STATUS,idStatus);
        return db.update(TABLE_NAME,cv,CLMN_ID_VEHICULO+"=?",new String[]{idVehiculo.toString()});
    }

}
