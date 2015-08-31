package com.example.mou.Utilerias;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mou.Enum.TipoImportanciaToast;
import com.example.mou.prueba.R;

/**
 * Created by Mou on 18/01/2015.
 */
public class MsgToast {

    private String mensaje;
    private boolean tiempo_largo;
    Context context;


    /**
     * Método para generar un toast personalizado.
     * @param contexto
     * @param msj Mensaje que se va a mostrar.
     * @param largo TRUE si la duración es larga, FALSE si la duración es corta.
     */
    public MsgToast(Context contexto, String msj, boolean largo, int tipoToast) {

        LayoutInflater inflater = (LayoutInflater)contexto.getSystemService(contexto.LAYOUT_INFLATER_SERVICE);
        View customToastLayout;
        if(tipoToast == TipoImportanciaToast.ERROR.getId()){
            customToastLayout = inflater.inflate(R.layout.custom_toast_error, null);
        }else {
            customToastLayout = inflater.inflate(R.layout.custom_toast, null);
        }

        Toast customToast = new Toast(contexto);
        customToast.setView(customToastLayout);
        customToast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
        if(largo) {
            customToast.setDuration(Toast.LENGTH_LONG);
        } else{
            customToast.setDuration(Toast.LENGTH_SHORT);
        }
        TextView msg = (TextView)customToastLayout.findViewById(R.id.toast_tvMensaje);
        msg.setText(msj);
        customToast.show();
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }



}
