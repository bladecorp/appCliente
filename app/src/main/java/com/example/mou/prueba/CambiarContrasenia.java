package com.example.mou.prueba;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.mou.Enum.TipoImportanciaToast;
import com.example.mou.Utilerias.InformacionLocal;
import com.example.mou.Utilerias.DialogoConfirmacion;
import com.example.mou.Utilerias.MsgToast;


public class CambiarContrasenia extends ActionBarActivity {

    TextView tvContr;
    EditText etContr;
    String contraseniaActual;
    MenuItem itemEditar, itemCancelar, itemGuardar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cambiar_contrasenia);
        init();
    }

    public void init(){
        tvContr = (TextView)findViewById(R.id.tvPassword_CambiarContr);
        etContr = (EditText)findViewById(R.id.etContr_cambiarContr);
        contraseniaActual = InformacionLocal.obtenerPasswordLocal(CambiarContrasenia.this);
        tvContr.setText(contraseniaActual);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_cambiar_contrasenia, menu);
        itemEditar = menu.findItem(R.id.cambiar_contr);
        itemCancelar = menu.findItem(R.id.cambiar_contr_cancelar);
        itemGuardar = menu.findItem(R.id.cambiar_contr_guardar);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        int id = item.getItemId();
      //  MenuItem itemEditar = (MenuItem)findViewById(R.id.cambiar_contr);

        //noinspection SimplifiableIfStatement
        if (id == R.id.cambiar_contr) {
            try {
                DialogoConfirmacion.mostrarDialogoContrasenia(CambiarContrasenia.this, "Autorización", "Escriba su contraseña",
                        contraseniaActual, new Runnable() {
                            @Override
                            public void run() {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        tvContr.setVisibility(View.GONE);
                                        etContr.setVisibility(View.VISIBLE);
                                        itemEditar.setVisible(false);
                                        itemCancelar.setVisible(true);
                                        itemGuardar.setVisible(true);
                                    }
                                });
                            }
                        }, null);
            } catch (Exception e) {
                new MsgToast(getApplicationContext(), "Error en el diálogo de cambiar contraseña", false,
                        TipoImportanciaToast.ERROR.getId());
            }
        }
        if(id == R.id.cambiar_contr_guardar){
            if(!etContr.getText().toString().isEmpty()){
                InformacionLocal.guardarPasswordLocal(CambiarContrasenia.this, etContr.getText().toString());
                new MsgToast(this, "La contraseña se modificó exitosamente", false, TipoImportanciaToast.INFO.getId());
                startActivity(new Intent(CambiarContrasenia.this, MainActivity.class));
            }else{
                new MsgToast(this, "Debe escribir una contraseña", false, TipoImportanciaToast.ERROR.getId());
            }
        }
        if(id == R.id.cambiar_contr_cancelar){
            itemGuardar.setVisible(false);
            itemCancelar.setVisible(false);
            itemEditar.setVisible(true);
            tvContr.setVisibility(View.VISIBLE);
            etContr.setText("");
            etContr.setVisibility(View.GONE);
            tvContr.setText(contraseniaActual);
        }
        return super.onOptionsItemSelected(item);
    }
}
