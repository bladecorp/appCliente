package com.example.mou.Utilerias;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Mou on 11/08/2015.
 */
public class InformacionLocal {

    public static String obtenerPasswordLocal(Context contexto){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(contexto);
        return sp.getString("password","");
    }

    public static void guardarPasswordLocal(Context contexto, String password){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(contexto);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("password",password);
        editor.commit();
    }

    public static String obtenerUltimaFechaActualizacion(Context contexto){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(contexto);
        return sp.getString("fechaActualizacion","No disponible");
    }

    public static void guardarUltimaFechaActualizacion(Context contexto, Calendar calendario){
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyy, HH:mm:ss");
        String fecha = "";
        if(calendario==null){
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE,0);
            fecha = sdf.format(cal.getTime());
        }else{
            fecha = sdf.format(calendario.getTime());
        }
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(contexto);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("fechaActualizacion",fecha);
        editor.commit();
    }

    public static int obtenerStatusComunicacion(Context contexto){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(contexto);
        return sp.getInt("statuComunicacion",0);
    }

    public static void guardarStatusComunicacion(Context contexto, Integer status){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(contexto);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("statuComunicacion",status);
        editor.commit();
    }

    public static void guardarTelefonoProveedor(Context contexto, String telefono){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(contexto);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("telProveedor",telefono);
        editor.commit();
    }

    public static String obtenerTelefonoProveedor(Context contexto){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(contexto);
        return sp.getString("telProveedor","");
    }

    public static int obtenerStatusConexion(Context contexto){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(contexto);
        return sp.getInt("statuConexion",0);
    }

    public static void guardarStatusConexion(Context contexto, Integer status){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(contexto);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("statuConexion",status);
        editor.commit();
    }

}
