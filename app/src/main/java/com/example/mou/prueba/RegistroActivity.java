
package com.example.mou.prueba;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.mou.Enum.TipoImportanciaToast;
import com.example.mou.Enum.TipoUsuarioEnum;
import com.example.mou.Servicio.WebServ;
import com.example.mou.Utilerias.DialogoCargando;
import com.example.mou.Utilerias.MsgToast;
import com.example.mou.data.BaseDatos;
import com.example.mou.data.DestinatarioData;
import com.example.mou.data.HistoricoMensajeData;
import com.example.mou.data.LlavesData;
import com.example.mou.data.TelefonoData;
import com.example.mou.data.UsuarioData;
import com.example.mou.data.VehiculoData;
import com.example.mou.dto.UsuarioDTO;
import com.example.mou.model.Llave;
import com.example.mou.model.Telefono;
import com.example.mou.model.TipoUsuario;
import com.example.mou.model.Usuario;


public class RegistroActivity extends ActionBarActivity implements View.OnClickListener{

    TextView tvCuentaTitular, tvUsuario,tvPassword;
    Button btnLogin;
    EditText etUsuario, etPassword;
    UsuarioDTO usuarioDTO;
    Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        init();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.reg_tvCuentaTitular:
                startActivity(new Intent(RegistroActivity.this, RegistrarCuenta.class));
                break;
            case R.id.reg_btnLogin:
                if(validarUsuario()) {
                    DialogoCargando.mostrarDialogo(RegistroActivity.this);
                    enviarTransaccionUsuario();
                }
                break;
        }
    }

    public boolean validarUsuario(){
        if(etUsuario.getText().toString().isEmpty()){
            new MsgToast(this,"Debe escribir un nombre de usuario", false, TipoImportanciaToast.ERROR.getId());
            return false;
        }
        if(etPassword.getText().toString().isEmpty()){
            new MsgToast(this,"Debe escribir la contraseña", false, TipoImportanciaToast.ERROR.getId());
            return false;
        }
        return true;
    }

    public boolean validarPassword(String contrasenia){
        if(usuarioDTO.getUsuario().getContrasena().contentEquals(contrasenia)){
            return true;
        }
        return false;
    }

    public boolean validarCampos(){
        Usuario us = usuarioDTO.getUsuario();
        if(us.getIdUsuario()==null || us.getNombre()==null || us.getaPaterno()==null || us.getUsuario()==null ||
            us.getContrasena()==null) {
            return false;
        }
        if(us.getNombre().isEmpty() || us.getaPaterno().isEmpty() || us.getUsuario().isEmpty() ||
                  us.getContrasena().isEmpty()) {
            return false;
        }

        for(Telefono tel : usuarioDTO.getTelefonos()){
            if((tel.getId()==null || tel.getIdUsuario()==null || tel.getTelefono()==null)){
                return false;
            }
            if(tel.getTelefono().isEmpty()){
                return false;
            }
        }
        return true;
    }

    public void guardarInformacionLocal(){
        BaseDatos base = new BaseDatos(this);
        base.abrir();
        UsuarioData usData = new UsuarioData(base);
        usData.insertarUsuario(usuarioDTO.getUsuario());
        TelefonoData telData = new TelefonoData(base);
        telData.insertarTelefonos(usuarioDTO.getTelefonos());
        base.cerrar();
    }

    public void enviarTransaccionUsuario(){
        final String contr = etPassword.getText().toString();
        final WebServ webServ = new WebServ();
        usuarioDTO.getUsuario().setUsuario(etUsuario.getText().toString());
        try {
            webServ.obtenerInformacionUsuario(usuarioDTO,contr, new Runnable() {
                @Override
                public void run() {
                  runOnUiThread(new Runnable() {
                      @Override
                      public void run() {
                          boolean exito = validarPassword(contr);
                          if(exito){
                              exito = validarCampos();
                              if(exito){
                                guardarInformacionLocal();
                                new MsgToast(getApplicationContext(),"La cuenta se vinculó exitosamente", false,
                                          TipoImportanciaToast.INFO.getId());
                                DialogoCargando.ocultarDialogo();
                                startActivity(new Intent(RegistroActivity.this, MainActivity.class));
                              }else{
                                  DialogoCargando.ocultarDialogo();
                                  new MsgToast(getApplicationContext(),"La información llegó incompleta, intente de nuevo", false,
                                          TipoImportanciaToast.ERROR.getId());
                              }
                          }else{
                              DialogoCargando.ocultarDialogo();
                              new MsgToast(getApplicationContext(),"La contraseña es incorrecta", false,
                                      TipoImportanciaToast.ERROR.getId());
                          }
                      }
                  });
                }
            }, new Runnable(){
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            DialogoCargando.ocultarDialogo();
                            new MsgToast(getApplicationContext(), "Error de comunicación con el Web Service", false,
                                    TipoImportanciaToast.ERROR.getId());
                        }
                    });
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void init(){
        obtenerUsuarioLocal();

        tvUsuario = (TextView)findViewById(R.id.reg_tvUsuarioOnombre);
        tvPassword = (TextView)findViewById(R.id.reg_tvPassword);

        tvCuentaTitular = (TextView)findViewById(R.id.reg_tvCuentaTitular);
        tvCuentaTitular.setClickable(true);
        tvCuentaTitular.setOnClickListener(this);

        etUsuario = (EditText)findViewById(R.id.reg_etUsuario);
        etPassword = (EditText)findViewById(R.id.reg_etPassword);

        btnLogin = (Button) findViewById(R.id.reg_btnLogin);
        btnLogin.setOnClickListener(this);

        establecerFormatoSegunUsuario();
        usuarioDTO = new UsuarioDTO();
    }

    public void establecerFormatoSegunUsuario(){
        if(usuario != null){//SI EL USUARIO ES NULL, EL FORMATO SERÁ POR EL QUE VIENE POR DEFAULT
            tvUsuario.setVisibility(View.VISIBLE);
            tvPassword.setVisibility(View.VISIBLE);
            etUsuario.setVisibility(View.GONE);
            etPassword.setVisibility(View.GONE);
            btnLogin.setVisibility(View.GONE);
            tvCuentaTitular.setVisibility(View.GONE);
            tvUsuario.setText(usuario.getUsuario());
            tvPassword.setText(usuario.getContrasena());
        }
    }

    public void obtenerUsuarioLocal(){
        BaseDatos base = new BaseDatos(this);
        base.abrir();
        UsuarioData usData = new UsuarioData(base);
        usuario = usData.obtenerDatosPersonales();
        base.cerrar();
    }

    public void borrarDatosDeUsuario(){
        BaseDatos base = new BaseDatos(this);
        base.abrir();
        HistoricoMensajeData hmd = new HistoricoMensajeData(base);
        hmd.eliminarPorTipoUsuarioYvehiculo(TipoUsuarioEnum.TITULAR.getId(), null);
        DestinatarioData destData = new DestinatarioData(base);
        destData.eliminarTodo();
        LlavesData llavesData = new LlavesData(base);
        llavesData.eliminarPorTipoUsuario(TipoUsuarioEnum.TITULAR.getId());
        VehiculoData vehData = new VehiculoData(base);
        vehData.borrarPorTipoUsuario(TipoUsuarioEnum.TITULAR.getId());
        TelefonoData telData = new TelefonoData(base);
        telData.eliminarTodo();
        UsuarioData usData = new UsuarioData(base);
        usData.eliminarTodo();
        base.cerrar();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_registro, menu);

        if(usuario==null) {
            menu.removeItem(R.id.cerrar_sesion);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.cerrar_sesion) {
            borrarDatosDeUsuario();
            new MsgToast(getApplicationContext(), "La sesión se cerró correctamente", false, TipoImportanciaToast.INFO.getId());
            startActivity(new Intent(RegistroActivity.this, MainActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

}
