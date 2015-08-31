package com.example.mou.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.mou.model.HistoricoMensajes;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mou on 05/08/2015.
 */
public class HistoricoMensajeData {

    private static final String CLMN_ID = "id";
    private static final String CLMN_MENSAJE = "mensaje";
    private static final String CLMN_ID_TIPOMENSAJE = "idTipoMensaje";
    private static final String CLMN_ID_SUBMENSAJE = "idSubMensaje";
    private static final String CLMN_FECHA = "fecha";
    private static final String CLMN_IDVEHICULO = "idVehiculo";
    private static final String CLMN_ID_TIPOUSUARIO = "tipoUsuario";

    public static final String TABLE_NAME = "historicomensaje";

    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " ("
            +CLMN_ID+" INTEGER PRIMARY KEY, "
            +CLMN_MENSAJE+" TEXT, "
            +CLMN_ID_TIPOMENSAJE+" INTEGER, "
            +CLMN_ID_SUBMENSAJE+" INTEGER, "
            +CLMN_FECHA+" TEXT, "
            +CLMN_IDVEHICULO+" INTEGER, "
            +CLMN_ID_TIPOUSUARIO+" INTEGER"
            +");";

    private SQLiteDatabase db;

    public HistoricoMensajeData(BaseDatos miBase) {
        db = miBase.getWritableDatabase();
    }

    public List<HistoricoMensajes> obtenerMensajesPorIdVehiculo(Integer idVehiculo){
        List<HistoricoMensajes> mensajes = new ArrayList<>();
        if(idVehiculo!=null){
            Cursor c = db.rawQuery("SELECT * FROM "+TABLE_NAME+" WHERE "+CLMN_IDVEHICULO+" =?", new String[]{idVehiculo.toString()});
            while(c.moveToNext()){
                HistoricoMensajes histMensaje = new HistoricoMensajes();
                histMensaje.setId(c.getInt(c.getColumnIndex(CLMN_ID)));
                histMensaje.setMensaje(c.getString(c.getColumnIndex(CLMN_MENSAJE)));
                histMensaje.setIdTipoMensaje(c.getInt(c.getColumnIndex(CLMN_ID_TIPOMENSAJE)));
                histMensaje.setIdSubMensaje(c.getInt(c.getColumnIndex(CLMN_ID_SUBMENSAJE)));
                histMensaje.setFecha(c.getString(c.getColumnIndex(CLMN_FECHA)));
                histMensaje.setIdVehiculo(c.getInt(c.getColumnIndex(CLMN_IDVEHICULO)));
                histMensaje.setTipoUsuario(c.getInt(c.getColumnIndex(CLMN_ID_TIPOUSUARIO)));
                mensajes.add(histMensaje);
            }
        }
        return mensajes;
    }

    public HistoricoMensajes obtenerHistoricoMensajePorId(Integer idHistMensaje){
        if(idHistMensaje!=null){
            Cursor c = db.rawQuery("SELECT * FROM "+TABLE_NAME+" WHERE "+CLMN_ID+" =?", new String[]{idHistMensaje.toString()});
            if(c.moveToFirst()){
                HistoricoMensajes histMensaje = new HistoricoMensajes();
                histMensaje.setId(c.getInt(c.getColumnIndex(CLMN_ID)));
                histMensaje.setMensaje(c.getString(c.getColumnIndex(CLMN_MENSAJE)));
                histMensaje.setIdTipoMensaje(c.getInt(c.getColumnIndex(CLMN_ID_TIPOMENSAJE)));
                histMensaje.setIdSubMensaje(c.getInt(c.getColumnIndex(CLMN_ID_SUBMENSAJE)));
                histMensaje.setFecha(c.getString(c.getColumnIndex(CLMN_FECHA)));
                histMensaje.setIdVehiculo(c.getInt(c.getColumnIndex(CLMN_IDVEHICULO)));
                histMensaje.setTipoUsuario(c.getInt(c.getColumnIndex(CLMN_ID_TIPOUSUARIO)));
                return histMensaje;
            }
        }
        return null;
    }

    public void eliminarTodo(){
        db.delete(TABLE_NAME,null, null);
    }

    public void eliminarPorTipoUsuarioYvehiculo(Integer tipoUsuario, Integer idVehiculo){
        if(idVehiculo != null) {
            db.delete(TABLE_NAME, CLMN_ID_TIPOUSUARIO + "=? AND " + CLMN_IDVEHICULO + "=?",
                    new String[]{tipoUsuario.toString(), idVehiculo.toString()});
        }else{
            db.delete(TABLE_NAME, CLMN_ID_TIPOUSUARIO + "=?",new String[]{tipoUsuario.toString()});
        }
    }

    public void insertarHistoricos(HistoricoMensajes hist){
        if(hist != null){
            ContentValues cv = new ContentValues();
            cv.put(CLMN_ID, hist.getId());
            cv.put(CLMN_FECHA, hist.getFecha());
            cv.put(CLMN_ID_TIPOMENSAJE, hist.getIdTipoMensaje());
            cv.put(CLMN_ID_SUBMENSAJE, hist.getIdSubMensaje());
            cv.put(CLMN_MENSAJE, hist.getMensaje());
            cv.put(CLMN_IDVEHICULO, hist.getIdVehiculo());
            cv.put(CLMN_ID_TIPOUSUARIO, hist.getTipoUsuario());
            db.insert(TABLE_NAME, null, cv);
        }
    }


}// fin clase
