package com.example.mou.prueba;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.mou.Enum.ComunicacionEnum;
import com.example.mou.Enum.TipoImportanciaToast;
import com.example.mou.Utilerias.DialogoCargando;
import com.example.mou.Utilerias.InformacionLocal;
import com.example.mou.Utilerias.DialogoConfirmacion;
import com.example.mou.Servicio.WebServ;
import com.example.mou.data.BaseDatos;
import com.example.mou.data.TelefonoData;
import com.example.mou.data.UsuarioData;
import com.example.mou.Utilerias.MsgToast;
import com.example.mou.model.Telefono;
import com.example.mou.model.Usuario;

import java.util.ArrayList;
import java.util.List;


public class DatosPersonales extends ActionBarActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    TextView tvNombre,tvApaterno, tvAmaterno, tvContr, tvContrLocal, tvUsuario;
    EditText etNombre, etContr, etContrLocal, etApaterno, etAmaterno;
    Spinner spinner;
    Button btnGuardar;
    ImageButton btnAgregarTel, btnBorrarTel, btnEditarTel;
    MenuItem menuEditar, menuCancelar;
    Usuario usuario;
    List<Telefono> telefonos;
    String telefonoS;
    Telefono telefono;
    ArrayAdapter<Telefono> adaptadorTels;
    ArrayAdapter<Telefono> telefonosProv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datos_personales);
        init();
        mostrarDatosPersonales();
        if(InformacionLocal.obtenerStatusComunicacion(this)== ComunicacionEnum.SIN_COMUNICACION.getId()){
            new MsgToast(this, "No se podrá editar la información, ya que no hay comunicación con el Web Service",
                    true, TipoImportanciaToast.ERROR.getId());
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.menu_datos_personales, menu);
        menuEditar = menu.findItem(R.id.menu_dp_editar);
        menuCancelar = menu.findItem(R.id.menu_dp_cancelar);
        menuCancelar.setVisible(false);
        if(InformacionLocal.obtenerStatusComunicacion(this)== ComunicacionEnum.SIN_COMUNICACION.getId()){
           menuEditar.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_dp_editar) {
            try {
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
                String contrLocal = sp.getString("password","N/D");
                DialogoConfirmacion.mostrarDialogoContrasenia(this, "Confirmación", "Por favor ingrese su contraseña", contrLocal,
                        new Runnable() {
                            @Override
                            public void run() {
                                hacerCamposEditables();
                            }
                        }, null);
            }catch(Exception ex){
                new MsgToast(this, "Error al mostrar el diálogo", false, TipoImportanciaToast.ERROR.getId());
            }
        }
        if(id == R.id.menu_dp_cancelar){
            fijarCampos();
        }
        if(id == R.id.menu_dp_telProv){
            telefonosProv.clear();
            mostrarDialogoTelefonoProveedor();
        }
  //      return true;
        return super.onOptionsItemSelected(item);
    }

    public void obtenerDatosPersonales(){
        BaseDatos base = new BaseDatos(this);
        base.abrir();
        UsuarioData usuarioData = new UsuarioData(base);
        usuario = usuarioData.obtenerDatosPersonales();
        base.cerrar();
        obtenerTelefonosDeUsuario();
    }

    public void obtenerTelefonosDeUsuario(){
        BaseDatos base = new BaseDatos(this);
        base.abrir();
        TelefonoData telData = new TelefonoData(base);
        telefonos = telData.obtenerTelefonosPorIdUsuario(usuario.getIdUsuario());
        base.cerrar();
    }

    public void mostrarDatosPersonales(){
        obtenerDatosPersonales();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        tvUsuario.setText(usuario.getUsuario());
        tvNombre.setText(usuario.getNombre());
        tvApaterno.setText(usuario.getaPaterno());
        tvAmaterno.setText(usuario.getaMaterno());
        tvContr.setText(usuario.getContrasena());
        tvContrLocal.setText(sp.getString("password","N/D"));
        adaptadorTels = new ArrayAdapter<Telefono>(this, android.R.layout.simple_list_item_1, telefonos);
        telefonosProv = new ArrayAdapter<Telefono>(this, android.R.layout.simple_list_item_1, telefonos);
        spinner.setAdapter(adaptadorTels);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_dP_Guardar:
                if(validarDatos()){
                    dialogoConfirmarEdicion();
                }
                break;
            case R.id.btn_dP_agregarTel:
                dialogoAgregarTelefono();
                break;
            case R.id.btn_dP_borrarTel:
                if(telefonos.size() > 1) {
                    eliminarTelefono();
                }else{new MsgToast(this,"Debe haber al menos un teléfono registrado", false, TipoImportanciaToast.ERROR.getId());}
                break;
            case R.id.btn_dP_editarTel:
                editarTelefono();
                break;
        }
    }

    public void init(){
        tvUsuario = (TextView)findViewById(R.id.txt_dP_usuario);
        tvNombre = (TextView)findViewById(R.id.txt_dP_nombre);
        tvApaterno = (TextView)findViewById(R.id.txt_dP_apaterno);
        tvAmaterno = (TextView)findViewById(R.id.txt_dP_amaterno);
        tvContr = (TextView)findViewById(R.id.txt_dP_contr);
        tvContrLocal = (TextView)findViewById(R.id.txt_dP_contrLocal);

        etNombre = (EditText)findViewById(R.id.et_dP_nombre);
        etApaterno = (EditText)findViewById(R.id.et_dP_apaterno);
        etAmaterno = (EditText)findViewById(R.id.et_dP_amaterno);
        etContr = (EditText)findViewById(R.id.et_dP_password);
        etContrLocal = (EditText)findViewById(R.id.et_dP_passwordLocal);

        btnGuardar = (Button)findViewById(R.id.btn_dP_Guardar);
        btnGuardar.setOnClickListener(this);
        btnGuardar.setVisibility(View.INVISIBLE);
        btnAgregarTel = (ImageButton)findViewById(R.id.btn_dP_agregarTel);
        btnAgregarTel.setOnClickListener(this);
        btnBorrarTel = (ImageButton)findViewById(R.id.btn_dP_borrarTel);
        btnBorrarTel.setOnClickListener(this);
        btnEditarTel = (ImageButton)findViewById(R.id.btn_dP_editarTel);
        btnEditarTel.setOnClickListener(this);

        telefonos = new ArrayList<>();
        spinner = (Spinner)findViewById(R.id.spinnerTels);
        spinner.setOnItemSelectedListener(this);
        telefonoS = "";
        telefono = new Telefono();

    }

    public void hacerCamposEditables(){
        menuCancelar.setVisible(true);
        menuEditar.setVisible(false);
        btnGuardar.setVisibility(View.VISIBLE);

        tvNombre.setVisibility(View.GONE);
        tvApaterno.setVisibility(View.GONE);
        tvAmaterno.setVisibility(View.GONE);
        tvContr.setVisibility(View.GONE);
        tvContrLocal.setVisibility(View.GONE);

        etNombre.setVisibility(View.VISIBLE);
        etApaterno.setVisibility(View.VISIBLE);
        etAmaterno.setVisibility(View.VISIBLE);
        etContr.setVisibility(View.VISIBLE);
        etContrLocal.setVisibility(View.VISIBLE);
        btnAgregarTel.setVisibility(View.VISIBLE);
        btnEditarTel.setVisibility(View.VISIBLE);
        btnBorrarTel.setVisibility(View.VISIBLE);
        btnGuardar.setEnabled(true);
        etNombre.setText(usuario.getNombre());
        etApaterno.setText(usuario.getaPaterno());
        etAmaterno.setText(usuario.getaMaterno());
        etContr.setText(usuario.getContrasena());
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        etContrLocal.setText(sp.getString("password","N/D"));

    }

    public void fijarCampos(){
        menuCancelar.setVisible(false);
        menuEditar.setVisible(true);
        btnGuardar.setVisibility(View.INVISIBLE);

        tvNombre.setVisibility(View.VISIBLE);
        tvApaterno.setVisibility(View.VISIBLE);
        tvAmaterno.setVisibility(View.VISIBLE);
        tvContr.setVisibility(View.VISIBLE);
        tvContrLocal.setVisibility(View.VISIBLE);

        etNombre.setVisibility(View.GONE);
        etApaterno.setVisibility(View.GONE);
        etAmaterno.setVisibility(View.GONE);
        etContr.setVisibility(View.GONE);
        etContrLocal.setVisibility(View.GONE);
        btnAgregarTel.setVisibility(View.GONE);
        btnBorrarTel.setVisibility(View.GONE);
        btnEditarTel.setVisibility(View.GONE);

        mostrarDatosPersonales();
        btnGuardar.setEnabled(false);
    }

    public void guardarDatosWS(){
        DialogoCargando.mostrarDialogo(DatosPersonales.this);
        WebServ ws = new WebServ();
        try {
            ws.actualizarUsuarioYtelefonos(usuario, telefonos, new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            guardarDatosLocales();
                            InformacionLocal.guardarPasswordLocal(DatosPersonales.this, etContrLocal.getText().toString());
                            obtenerDatosPersonales();
                            fijarCampos();
                            DialogoCargando.ocultarDialogo();
                            new  MsgToast(getApplicationContext(),"La información se actualizó exitosamente", false,
                                    TipoImportanciaToast.INFO.getId());
                        }
                    });
                }
            },new Runnable() {
                @Override
                public void run() {
                    DialogoCargando.ocultarDialogo();
                    mensajeErrorEnTransaccion();
                }
            });
        } catch (Exception e) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    DialogoCargando.ocultarDialogo();
                    new  MsgToast(getApplicationContext(),"Error al actualizar la información, intente de nuevo", false,
                            TipoImportanciaToast.ERROR.getId());
                }
            });
        }
    }

    public void guardarDatosLocales(){
        BaseDatos base = new BaseDatos(this);
        base.abrir();
        UsuarioData usData = new UsuarioData(base);
        usData.eliminarTodo();
        usData.insertarUsuario(usuario);
        TelefonoData telData = new TelefonoData(base);
        telData.eliminarTodo();
        telData.insertarTelefonos(telefonos);
        base.cerrar();
    }

    public boolean validarDatos() {
        if (etNombre.getText().toString().isEmpty()) {
            new MsgToast(this, "Debe escribir su nombre", false, TipoImportanciaToast.ERROR.getId());
            return false;
        }
        if(etApaterno.getText().toString().isEmpty()){
            new MsgToast(this, "Debe escribir su apellido paterno", false, TipoImportanciaToast.ERROR.getId());
            return false;
        }
        if(etAmaterno.getText().toString().isEmpty()){
            new MsgToast(this, "Debe escribir su apellido materno", false, TipoImportanciaToast.ERROR.getId());
            return false;
        }
        if(etContr.getText().toString().isEmpty()){
            new MsgToast(this, "Debe escribir la contraseña", false, TipoImportanciaToast.ERROR.getId());
            return false;
        }
        if(etContrLocal.getText().toString().isEmpty()){
            new MsgToast(this, "Debe escribir la contraseña local", false, TipoImportanciaToast.ERROR.getId());
            return false;
        }

        return true;
    }

    public void dialogoConfirmarEdicion(){
        try {
            DialogoConfirmacion.mostrarDialogo(this, "Confirmación", "¿Desea guardar los cambios?", "Guardar", "Cancelar",
                    new Runnable() {
                        @Override
                        public void run() {
                            usuario.setContrasena(etContr.getText().toString());
                            usuario.setNombre(etNombre.getText().toString());
                            usuario.setaPaterno(etApaterno.getText().toString());
                            usuario.setaMaterno(etAmaterno.getText().toString()!=null?etAmaterno.getText().toString():"");
                            for(Telefono t : telefonos){
                                t.setId(0);
                            }
                            guardarDatosWS();

                        }
                    }, null);
        }catch(Exception ex){
            new MsgToast(this, "Error al mostrar el diálogo de confirmación", false, TipoImportanciaToast.ERROR.getId());
        }
    }

    public void mostrarDialogoTelefonoProveedor(){
        try {
            DialogoConfirmacion.mostrarDialogoTelefono(this, "Agregar Telefono", "Ingrese el teléfono al que desea llamar cuando reciba una alarma",
                    new Runnable() {
                        @Override
                        public void run() {
                            InformacionLocal.guardarTelefonoProveedor(DatosPersonales.this, telefonosProv.getItem(0).getTelefono());
                        }
                    }, null,telefonosProv,usuario.getIdUsuario(),null);
        } catch (Exception e) {
            new MsgToast(this, "Error al mostrar el diálogo", false, TipoImportanciaToast.ERROR.getId());
        }
    }

    public void dialogoAgregarTelefono(){
        try {
            DialogoConfirmacion.mostrarDialogoTelefono(this, "Agregar Teléfono", "Escriba un teléfono de 10 dígitos",
                    new Runnable() {
                @Override
                public void run() {
                }
            },null, adaptadorTels, usuario.getIdUsuario(), null);
        } catch (Exception e) {
            new MsgToast(getApplicationContext(),"Error en el diálogo: "+e.getMessage(),false,TipoImportanciaToast.ERROR.getId());
        }
    }

    public void editarTelefono(){
        try {
            DialogoConfirmacion.mostrarDialogoTelefono(this, "Editar Teléfono", "Escriba un teléfono de 10 dígitos",
                    new Runnable() {
                        @Override
                        public void run() {
                            telefono = telefonos.get(0);
                        }
                    },null, adaptadorTels, usuario.getIdUsuario(), telefono);
        } catch (Exception e) {
            new MsgToast(getApplicationContext(),"Error en el diálogo: "+e.getMessage(),false,TipoImportanciaToast.ERROR.getId());
        }
    }

    public void eliminarTelefono(){

        try {
            DialogoConfirmacion.mostrarDialogo(this,"Confirmación","¿Desea eliminar este teléfono?\n\n"+telefono.getTelefono(),
                    "Borrar","Cancelar", new Runnable() {
                        @Override
                        public void run() {
                            adaptadorTels.remove(telefono);
                            telefono = telefonos.get(0);
                            spinner.setSelection(0, false);
                        }
                    },null);
        } catch (Exception e) {
            new MsgToast(getApplicationContext(),"Error en el diálogo: "+e.getMessage(),false,TipoImportanciaToast.ERROR.getId());
        }
    }

    public void mensajeErrorEnTransaccion(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new MsgToast(getApplicationContext(),"Ocurrió un error de comunicación con el Web Service"
                        , true, TipoImportanciaToast.ERROR.getId());
            }
        });
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        telefono = telefonos.get(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onBackPressed() {
        finish();
    }

}
