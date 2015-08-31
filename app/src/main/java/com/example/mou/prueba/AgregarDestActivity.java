package com.example.mou.prueba;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.example.mou.Enum.StatusInvitacion;
import com.example.mou.Enum.TipoImportanciaToast;
import com.example.mou.Servicio.WebServ;
import com.example.mou.Utilerias.DialogoCargando;
import com.example.mou.Utilerias.DialogoConfirmacion;
import com.example.mou.Utilerias.MsgToast;
import com.example.mou.Utilerias.ValidarTelefono;
import com.example.mou.data.BaseDatos;
import com.example.mou.data.DestinatarioData;
import com.example.mou.model.Destinatario;

import java.util.ArrayList;
import java.util.List;


public class AgregarDestActivity extends ActionBarActivity {

    TextView tvTitulo;
    EditText etNombre, etApaterno, etAmaterno, etTelefono;
    Integer idDestinatario;
    Integer idVehiculo;
    Destinatario destinatario;
    List<Destinatario> destinatarios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_dest);
        init();
    }

    private void init(){
        tvTitulo = (TextView)findViewById(R.id.agregarDest_tvTitulo);
        etNombre = (EditText)findViewById(R.id.agregarDest_etNombre);
        etApaterno = (EditText)findViewById(R.id.agregarDest_etApaterno);
        etAmaterno = (EditText)findViewById(R.id.agregarDest_etAmaterno);
        etTelefono = (EditText)findViewById(R.id.agregarDest_etTelefono);
        etTelefono.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        idDestinatario = getIntent().getIntExtra("idDestinatario",-1);
        idVehiculo = getIntent().getIntExtra("idVehiculo",-1);
        destinatarios = new ArrayList<>();
        if(idDestinatario != -1){
            obtenerDestinatario();
            llenarCampos();
        }else{
            tvTitulo.setText("AGREGAR DESTINATARIO");
        }

    }

    private void mostrarMensajeConfirmacionEditar(){
        try {
            DialogoConfirmacion.mostrarDialogo(AgregarDestActivity.this,"Confirmación","¿Desea guardar los cambios" +
                            " que realizó al destinatario?",
                    "Guardar","Cancelar", new Runnable() {
                        @Override
                        public void run() {
                            registrarDstinatarioEnWS(true);
                        }
                    }, null);
        } catch (Exception e) {
            new MsgToast(this, "Error al crear el diálogo ",false, TipoImportanciaToast.ERROR.getId());

        }
    }

    private void mostrarMensajeConfirmacion(){
        try {
            DialogoConfirmacion.mostrarDialogo(AgregarDestActivity.this,"Confirmación","¿Desea registrar este destinatario?",
                    "Registrar","Cancelar", new Runnable() {
                        @Override
                        public void run() {
                            registrarDstinatarioEnWS(false);
                        }
                    }, null);
        } catch (Exception e) {
            new MsgToast(this, "Error al crear el diálogo ",false, TipoImportanciaToast.ERROR.getId());

        }
    }

    private void registrarDstinatarioEnWS(boolean esEditar){
        DialogoCargando.mostrarDialogo(AgregarDestActivity.this);
        destinatarios = new ArrayList<>();
        WebServ ws = new WebServ();
        ws.registrarDestinatarioEnWS(destinatario,destinatarios,esEditar, new Runnable() {
            @Override
            public void run() {
                if(validacionDatosWS()) {
                    guardarInformacionLocal();
                    DialogoCargando.ocultarDialogo();
                }else{
                    DialogoCargando.ocultarDialogo();
                    mensajeInformacionIncompleta();
                }
            }
        }, new Runnable() {
            @Override
            public void run() {
                DialogoCargando.ocultarDialogo();
                mensajeDestinatarioDuplicado();
            }
        }, new Runnable() {
            @Override
            public void run() {
                DialogoCargando.ocultarDialogo();
                mensajeErrorEnTransaccion();
            }
        });
    }

    private void guardarInformacionLocal(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                BaseDatos base = new BaseDatos(AgregarDestActivity.this);
                base.abrir();
                DestinatarioData destData = new DestinatarioData(base);
                destData.eliminarDestinatarioPorIdVehiculo(idVehiculo);
                for(Destinatario dest : destinatarios) {
                    destData.insertarDestinatario(dest);
                }
                base.cerrar();
                if(idDestinatario == -1) {
                    new MsgToast(getApplicationContext(), "EL destinatario se registró exitosamente", false,
                            TipoImportanciaToast.INFO.getId());
                    Intent i = new Intent(AgregarDestActivity.this, DestinatariosActivity.class);
                    i.putExtra("idVehiculo", idVehiculo);
                    startActivity(i);
                }else{
                    new MsgToast(getApplicationContext(), "EL destinatario se actualizó exitosamente", false,
                            TipoImportanciaToast.INFO.getId());
                    Intent i = new Intent(AgregarDestActivity.this, DestinatariosActivity.class);
                    i.putExtra("idVehiculo", idVehiculo);
                    startActivity(i);
                }
            }
        });
    }

    private boolean validacionDatosWS(){
        for(Destinatario dest : destinatarios){
            if(dest.getId()==null || dest.getNombre()==null || dest.getaPaterno()==null || dest.getaMaterno()==null
                     || dest.getTelefono()==null || dest.getIdVehiculo()==null){
                return false;
            }
            if(dest.getId()<=0 || dest.getNombre().isEmpty() || dest.getaPaterno().isEmpty()
                    || dest.getTelefono().isEmpty() || dest.getIdVehiculo()<=0) {
                return false;
            }
        }
        return true;
    }

    private boolean validarDatos(){
        if(etNombre.getText().toString().isEmpty()){
            errorValidacionCampo("nombre");
            return false;
        }
        if(etApaterno.getText().toString().isEmpty()){
            errorValidacionCampo("apellido paterno");
            return false;
        }
        if(etTelefono.getText().toString().isEmpty()){
            errorValidacionCampo("telefono");
            return false;
        }
        if(!ValidarTelefono.validar10digitos(etTelefono.getText().toString())){
            new MsgToast(this, "El teléfono debe tener 10 dígitos", false, TipoImportanciaToast.ERROR.getId());
            return false;
        }
        destinatario = new Destinatario();
        destinatario.setNombre(etNombre.getText().toString());
        destinatario.setaPaterno(etApaterno.getText().toString());
        destinatario.setaMaterno(etAmaterno.getText().toString());
        destinatario.setTelefono(ValidarTelefono.obtener10digitos(etTelefono.getText().toString()));
        destinatario.setIdVehiculo(idVehiculo);
        destinatario.setStatusInv(StatusInvitacion.NO_ENVIADA.getId());
        destinatario.setId(idDestinatario!=-1?idDestinatario:0);
        return true;
    }

    private void llenarCampos(){
        tvTitulo.setText("EDITAR DESTINATARIO");
        etNombre.setText(destinatario.getNombre());
        etApaterno.setText(destinatario.getaPaterno());
        etAmaterno.setText(destinatario.getaMaterno());
        etTelefono.setText(destinatario.getTelefono());
    }

    private void obtenerDestinatario(){
        BaseDatos base  = new BaseDatos(this);
        base.abrir();
        DestinatarioData destData = new DestinatarioData(base);
        destinatario = destData.obtenerDestinatarioPorId(idDestinatario);
        base.cerrar();
    }

    private void errorValidacionCampo(String error){
        new MsgToast(this, "Debe escribir el "+error,false, TipoImportanciaToast.ERROR.getId());
    }

    private void mensajeDestinatarioDuplicado(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new MsgToast(getApplicationContext(), "No se pudo guardar el destinatario, posiblemente los " +
                  "datos están duplicados, revíselos e intente de nuevo", true, TipoImportanciaToast.ERROR.getId());
            }
        });
    }

    private void mensajeErrorEnTransaccion(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new MsgToast(getApplicationContext(), "Ocurrió un error de comunicación con el Web Service",
                        false, TipoImportanciaToast.ERROR.getId());
            }
        });
    }

    private void mensajeInformacionIncompleta(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new MsgToast(getApplicationContext(), "La información enviada por el Web Service llegó incompleta " +
                        ", intente de nuevo",
                        false, TipoImportanciaToast.ERROR.getId());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_agregar_dest, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_guardarDest) {
            if(validarDatos()){
                if(idDestinatario == -1) {
                    mostrarMensajeConfirmacion();
                }else{
                    mostrarMensajeConfirmacionEditar();
                }
            }
        }
        if(id == android.R.id.home){
            Intent i = new Intent();
            i.putExtra("idVehiculo", idVehiculo);
            setResult(RESULT_OK,i);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
