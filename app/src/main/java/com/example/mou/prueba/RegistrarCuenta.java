package com.example.mou.prueba;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mou.Enum.TipoImportanciaToast;
import com.example.mou.Servicio.WebServ;
import com.example.mou.Utilerias.DialogoCargando;
import com.example.mou.Utilerias.MsgToast;
import com.example.mou.Utilerias.ValidarTelefono;
import com.example.mou.data.BaseDatos;
import com.example.mou.data.TelefonoData;
import com.example.mou.data.UsuarioData;
import com.example.mou.model.Telefono;
import com.example.mou.model.Usuario;


public class RegistrarCuenta extends ActionBarActivity {

    EditText etNombre, etApaterno, etTelefono, etUsuario, etConstrasenia;
    Usuario usuario;
    String telefono;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_cuenta);
        init();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_registrar_cuenta, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.registro_menu_crear) {
            if(validacionGeneral()){
                DialogoCargando.mostrarDialogo(RegistrarCuenta.this);
                transaccionConWebService();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void init(){

        etNombre = (EditText)findViewById(R.id.registro_et_nombre);
        etApaterno = (EditText)findViewById(R.id.registro_et_paterno);
        etTelefono = (EditText)findViewById(R.id.registro_et_telefono);
        etTelefono.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        etUsuario = (EditText)findViewById(R.id.registro_et_usuario);
        etConstrasenia = (EditText)findViewById(R.id.registro_et_contr);
        usuario = new Usuario();
    }

    public void transaccionConWebService(){
        WebServ ws = new WebServ();
        ws.insertarUsuario(usuario, telefono, new Runnable() {
            @Override
            public void run() {
                guardarEnMovil();
            }
        }, new Runnable() {
            @Override
            public void run() {
                DialogoCargando.ocultarDialogo();
                mostrarErrorEnWebService();
            }
        });

    }

    public void guardarEnMovil(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(validacionIndiceObtenidoEnWs()){
                    boolean exito = registrarUsuarioLocal();
                    DialogoCargando.ocultarDialogo();
                    if(exito) {
                        startActivity(new Intent(RegistrarCuenta.this, MainActivity.class));
                    }
                }
            }
        });
    }

    public boolean validacionIndiceObtenidoEnWs(){
        if(usuario.getIdUsuario()==null || usuario.getIdUsuario()==-1){
            mostrarMensajeUsuarioRepetido();
            return false;
        }
        return true;
    }

    public boolean validacionGeneral(){
        if(etNombre.getText().toString().isEmpty()){
            new MsgToast(this, "Debe escribir su nombre", false, TipoImportanciaToast.ERROR.getId());
            return false;
        }
        if(etApaterno.getText().toString().isEmpty()){
            new MsgToast(this, "Debe escribir su apellido paterno", false, TipoImportanciaToast.ERROR.getId());
            return false;
        }
        if(etTelefono.getText().toString().isEmpty()){
            new MsgToast(this, "Debe escribir un número telefónico", false, TipoImportanciaToast.ERROR.getId());
            return false;
        }
        if(!ValidarTelefono.validar10digitos(etTelefono.getText().toString())){
            new MsgToast(this, "El teléfono debe tener 10 dígitos", false, TipoImportanciaToast.ERROR.getId());
            return false;
        }
        if(etUsuario.getText().toString().isEmpty()){
            new MsgToast(this, "Debe escribir un nombre de usuario", false, TipoImportanciaToast.ERROR.getId());
            return false;
        }
        if(etConstrasenia.getText().toString().isEmpty()){
            new MsgToast(this, "Debe escribir una contraseña", false, TipoImportanciaToast.ERROR.getId());
            return false;
        }
        usuario.setIdUsuario(0);
        usuario.setUsuario(etUsuario.getText().toString());
        usuario.setContrasena(etConstrasenia.getText().toString());
        usuario.setNombre(etNombre.getText().toString());
        usuario.setaPaterno(etApaterno.getText().toString());
        usuario.setaMaterno("");
        telefono = ValidarTelefono.obtener10digitos(etTelefono.getText().toString());
        return true;
    }//fin método validacionGeneral

    public boolean registrarUsuarioLocal(){

        BaseDatos base = new BaseDatos(this);
        base.abrir();
        UsuarioData usData = new UsuarioData(base);
        int exito = usData.insertarUsuario(usuario);
        if(exito != -1){
            new MsgToast(RegistrarCuenta.this, "La cuenta de usuario se creó exitosamente", false,
                    TipoImportanciaToast.INFO.getId());
            return true;
        }
        new MsgToast(RegistrarCuenta.this, "Ocurrió un error al sincronizar los datos con el dispositivo móvil, " +
                "intente de nuevo por favor", false,TipoImportanciaToast.ERROR.getId());
        base.cerrar();
        return false;
    }//fin metodo registrar

    public void mostrarErrorEnWebService(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new MsgToast(RegistrarCuenta.this, "Error de comunicación con Web Service", false,
                        TipoImportanciaToast.ERROR.getId());
            }
        });
    }

    private void mostrarMensajeUsuarioRepetido(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new MsgToast(RegistrarCuenta.this, "El nombre de usuario '"+usuario.getUsuario()+"' ya está en uso",
                        false, TipoImportanciaToast.ERROR.getId());
            }
        });
    }


}// fin clase
