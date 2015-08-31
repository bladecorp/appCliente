package com.example.mou.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.mou.model.Llave;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mou on 05/08/2015.
 */
public class LlavesData {

    private static final String CLMN_CODIGO = "codigo";
    private static final String CLMN_ID_TIPOLLAVE = "idtipollave";
    private static final String CLMN_IDVEHICULO = "idvehiculo";
    private static final String CLMN_ID_TIPOUSUARIO = "idtipousuario";

    public static final String TABLE_NAME = "llaves";

    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " ("
            +CLMN_CODIGO+" TEXT, "
            +CLMN_ID_TIPOLLAVE+" INTEGER, "
            +CLMN_IDVEHICULO+" INTEGER, "
            +CLMN_ID_TIPOUSUARIO+" INTEGER"
            +");";

    private SQLiteDatabase db;

    public LlavesData(BaseDatos miBase) {
        db = miBase.getWritableDatabase();
    }

    public List<Llave> obtenerLlavesPorIdVehiculo(Integer idVehiculo){
        List<Llave> llaves = new ArrayList<>();
        if(idVehiculo != null) {
            Cursor c = db.rawQuery("SELECT * FROM " + TABLE_NAME+" WHERE "+CLMN_IDVEHICULO+" =?", new String[]{idVehiculo.toString()});
            while (c.moveToNext()) {
                Llave llave = new Llave();
                llave.setLlave(c.getString(c.getColumnIndex(CLMN_CODIGO)));
                llave.setIdTipoLlave(c.getInt(c.getColumnIndex(CLMN_ID_TIPOLLAVE)));
                llave.setIdVehiculo(c.getInt(c.getColumnIndex(CLMN_IDVEHICULO)));
                llave.setTitular(c.getInt(c.getColumnIndex(CLMN_ID_TIPOUSUARIO)) == 1);
                llaves.add(llave);
            }
        }
        return llaves;
    }

    public void insertarLlave(Llave llave){
        if(llave != null){
            ContentValues cv = new ContentValues();
            cv.put(CLMN_CODIGO, llave.getLlave());
            cv.put(CLMN_ID_TIPOLLAVE, llave.getIdTipoLlave());
            cv.put(CLMN_IDVEHICULO, llave.getIdVehiculo());
            cv.put(CLMN_ID_TIPOUSUARIO,llave.isTitular()?1:0);
            db.insert(TABLE_NAME, null, cv);
        }
    }

    public void actualizarLlaves(Integer idVehiculo, List<Llave> llaves){
        if(idVehiculo!=null){
            eliminarLlavesPorIdVehiculo(idVehiculo);
            for(Llave llave : llaves){
                ContentValues cv = new ContentValues();
                cv.put(CLMN_IDVEHICULO,llave.getIdVehiculo());
                cv.put(CLMN_ID_TIPOLLAVE, llave.getIdTipoLlave());
                cv.put(CLMN_CODIGO, llave.getLlave());
                cv.put(CLMN_ID_TIPOUSUARIO, llave.isTitular()?1:0);
                db.update(TABLE_NAME, cv, CLMN_IDVEHICULO+"=? AND "+CLMN_ID_TIPOLLAVE+"=?",
                        new String[]{llave.getIdVehiculo().toString(), llave.getIdTipoLlave().toString()});
            }
        }
    }

    public void eliminarLlavesPorIdVehiculo(Integer idVehiculo){
        db.delete(TABLE_NAME,CLMN_IDVEHICULO+"=?", new String[]{idVehiculo.toString()});
    }

    public void eliminarTodo(){
        db.delete(TABLE_NAME,null, null);
    }

    public void eliminarPorTipoUsuario(Integer idTipoUsuario){
        if(idTipoUsuario!=null){
            db.delete(TABLE_NAME,CLMN_ID_TIPOUSUARIO+"=?",new String[]{idTipoUsuario.toString()});
        }
    }

}
