package com.example.mou.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.mou.model.Usuario;

/**
 * Created by Mou on 17/01/2015.
 */
public class UsuarioData {

    private static final String CLMN_ID = "idUsuario";
    private static final String CLMN_NOMBRE = "nombre";
    private static final String CLMN_APATERNO = "apaterno";
    private static final String CLMN_AMATERNO = "amaterno";
    private static final String CLMN_USUARIO = "usuario";
    private static final String CLMN_CONTRASENA = "contrasena";

    public static final String TABLE_NAME = "usuarios";

    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " ("
            +CLMN_ID+" INTEGER PRIMARY KEY, "
            +CLMN_NOMBRE+" TEXT, "
            +CLMN_APATERNO+" TEXT, "
            +CLMN_AMATERNO+" TEXT, "
            +CLMN_USUARIO+" TEXT, "
            +CLMN_CONTRASENA+ " TEXT"
            + ");";

    private SQLiteDatabase db;

    public UsuarioData(BaseDatos miBase){
        db = miBase.getWritableDatabase();
    }//

    public Usuario obtenerDatosPersonales(){
        Cursor cursor = db.rawQuery("SELECT * FROM "+TABLE_NAME, null);
        if(cursor.moveToNext()){
            Usuario usuario = new Usuario();
            usuario.setIdUsuario(cursor.getInt(cursor.getColumnIndex(CLMN_ID)));
            usuario.setNombre(cursor.getString(cursor.getColumnIndex(CLMN_NOMBRE)));
            usuario.setaPaterno(cursor.getString(cursor.getColumnIndex(CLMN_APATERNO)));
            usuario.setaMaterno(cursor.getString(cursor.getColumnIndex(CLMN_AMATERNO)));
            usuario.setUsuario(cursor.getString(cursor.getColumnIndex(CLMN_USUARIO)));
            usuario.setContrasena(cursor.getString(cursor.getColumnIndex(CLMN_CONTRASENA)));
            cursor.close();
            return usuario;
        } else {
            cursor.close();
            return null;
        }

    }

    public String obtenerContrasenaUsuario(){
        Cursor cursor = db.rawQuery("SELECT * FROM "+TABLE_NAME, null);
        if(cursor.moveToNext()){
            String contr = cursor.getString(cursor.getColumnIndex(CLMN_CONTRASENA));
            cursor.close();
            return contr;
        } else {
            cursor.close();
            return null;
        }
    }

    /**
     * Método que inserta un usuario en la base de datos.
     * @param usuario Recibe un objeto de la clase Usuario.
     * @return Devuelve el número de fila que se agregó o -1 si ocurrió algún error.
     * @throws Exception
     */
    public int insertarUsuario(Usuario usuario){
        ContentValues cv = generarContenido(usuario);
        if(cv != null){
            return (int)db.insert(TABLE_NAME, null, cv);
        } else{
            return -1;
        }
    }

    /**
     * Método para modificar un Usuario ya registrado
     * @param usuario Recibe el usuario con el id base y los nuevos párametros.
     * @return Devuelve el número de filas afectadas o 0 en caso de no hacer modificaciones.
     */
    public int actualizarUsuario(Usuario usuario){
        ContentValues cv = generarContenido(usuario);
        if(cv != null){
            return db.update(TABLE_NAME, cv, CLMN_ID+"=?", new String[] {usuario.getIdUsuario().toString()});
        } else{
            return 0;
        }
    }

    /**
     * Método que elimina al Usuario registrado.
     * @param idUsuario Recibe el id del Usuario.
     * @return Devuelve el número de filas afectadas o 0 en caso de no eliminar.
     */
    public int eliminarUsuarioPorId(Integer idUsuario){
       return db.delete(TABLE_NAME,CLMN_ID+"=?",new String[] {idUsuario.toString()});
    }

    public void eliminarTodo(){
        db.delete(TABLE_NAME,null, null);
    }

    private ContentValues generarContenido(Usuario usuario){
        if(usuario != null) {
            ContentValues cv = new ContentValues();
            cv.put(CLMN_ID, usuario.getIdUsuario());
            cv.put(CLMN_NOMBRE, usuario.getNombre());
            cv.put(CLMN_APATERNO, usuario.getaPaterno());
            cv.put(CLMN_AMATERNO, usuario.getaMaterno());
            cv.put(CLMN_USUARIO, usuario.getUsuario());
            cv.put(CLMN_CONTRASENA, usuario.getContrasena());
            return cv;
        } else{
            return null;
        }
    }
}
