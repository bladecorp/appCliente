package com.example.mou.Utilerias;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.Window;

import com.example.mou.prueba.R;

/**
 * Created by Mou on 15/08/2015.
 */
public class DialogoCargando {

    private static ProgressDialog pd;

    public static final void mostrarDialogo(Activity activity){
        pd = new ProgressDialog(activity, R.style.StyledDialog);
        pd.setMessage("Conectando con Web Service");
        pd.requestWindowFeature(Window.FEATURE_NO_TITLE);
        pd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pd.show();
    }

    public static final void ocultarDialogo(){
        pd.dismiss();
    }

}
