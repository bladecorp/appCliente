package com.example.mou.prueba;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.mou.Enum.TipoImportanciaToast;
import com.example.mou.Enum.TipoStatusEnum;
import com.example.mou.Servicio.WebServ;
import com.example.mou.Utilerias.DialogoCargando;
import com.example.mou.Utilerias.MsgToast;
import com.example.mou.data.BaseDatos;
import com.example.mou.data.CatalogoMarcas;
import com.example.mou.data.CatalogoModelos;
import com.example.mou.data.VehiculoData;
import com.example.mou.model.Marca;
import com.example.mou.model.Modelo;
import com.example.mou.model.TipoStatus;
import com.example.mou.model.Vehiculo;


public class StatusActivity extends ActionBarActivity implements View.OnLongClickListener{

    TextView tvVehiculo, tvPlacas, tvMensaje;
    Button btnStatus;
    Vehiculo vehiculo;
    String mensajeActivado = "La alarmas se recibirán normalmente";
    String mensajeDesactivado = "No se recibirán alarmas emitidas por este vehículo";
    String botonActivado = "Activado";
    String botonDesactivado = "Desactivado";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);
        init();
    }

    public void init(){
        tvVehiculo = (TextView)findViewById(R.id.status_tvVehiculo);
        tvPlacas = (TextView)findViewById(R.id.status_tvPlacas);
        tvMensaje = (TextView)findViewById(R.id.status_tvMensaje);
        btnStatus = (Button)findViewById(R.id.status_btnStatus);
        btnStatus.setOnLongClickListener(this);
        int idVehiculo = getIntent().getIntExtra("idVehiculo",-1);
        obtenerVehiculo(idVehiculo);
        tvVehiculo.setText(obtenerNombreMarcaYmodelo());
        tvPlacas.setText(vehiculo.getPlacas());
        if(vehiculo.getStatus() == TipoStatusEnum.ACTIVADO.getId()){
            tvMensaje.setText(mensajeActivado);
            btnStatus.setBackgroundResource(R.drawable.boton_status_activado);
            btnStatus.setText(botonActivado);
        }else{
            tvMensaje.setText(mensajeDesactivado);
        }
    }

    private void obtenerVehiculo(Integer idVehiculo){
        BaseDatos base = new BaseDatos(this);
        base.abrir();
        VehiculoData vehData = new VehiculoData(base);
        vehiculo = vehData.obtenerVehiculoPorId(idVehiculo);
        base.cerrar();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
      //  getMenuInflater().inflate(R.menu.menu_status, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()){
            case R.id.status_btnStatus:
                cambiarStatusEnWS();
                break;
        }
        return true;
    }

    private void cambiarStatusEnWS(){
        DialogoCargando.mostrarDialogo(StatusActivity.this);
        WebServ ws = new WebServ();
        int tipoStatus = vehiculo.getStatus()==TipoStatusEnum.ACTIVADO.getId()?TipoStatusEnum.DESACTIVADO.getId()
                :TipoStatusEnum.ACTIVADO.getId();
        ws.cambiarStatusEnWS(vehiculo.getIdVehiculo(),tipoStatus, new Runnable() {
            @Override
            public void run() {
                cambiarStatusLocal();
                DialogoCargando.ocultarDialogo();
                mensajeExitoAlCambiarStatus();
            }
        }, new Runnable() {
            @Override
            public void run() {
                DialogoCargando.ocultarDialogo();
                mensajeErrorAlCambiarStatus();
            }
        }, new Runnable() {
            @Override
            public void run() {
                DialogoCargando.ocultarDialogo();
                mostrarMensajeErrorWS();
            }
        });
    }

    private void cambiarStatusLocal(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                BaseDatos base = new BaseDatos(StatusActivity.this);
                base.abrir();
                VehiculoData vehData = new VehiculoData(base);
                int tipoStatus = vehiculo.getStatus()==TipoStatusEnum.ACTIVADO.getId()?TipoStatusEnum.DESACTIVADO.getId()
                        :TipoStatusEnum.ACTIVADO.getId();
                int exito = vehData.cambiarStatusPorIdVehiculo(vehiculo.getIdVehiculo(), tipoStatus);
                if(exito == 1) {
                    if (vehiculo.getStatus() == TipoStatusEnum.ACTIVADO.getId()) {
                        tvMensaje.setText(mensajeDesactivado);
                        btnStatus.setText(botonDesactivado);
                        btnStatus.setTextColor(getResources().getColor(R.color.White));
                        btnStatus.setBackgroundResource(R.drawable.boton_status);
                        vehiculo.setStatus(TipoStatusEnum.DESACTIVADO.getId());
                    } else {
                        tvMensaje.setText(mensajeActivado);
                        btnStatus.setText(botonActivado);
                        btnStatus.setTextColor(getResources().getColor(R.color.Black));
                        btnStatus.setBackgroundResource(R.drawable.boton_status_activado);
                        vehiculo.setStatus(TipoStatusEnum.ACTIVADO.getId());
                    }
                }else{
                    new MsgToast(StatusActivity.this, "No se pudo cambiar el status local", false, TipoImportanciaToast.ERROR.getId());
                }
                base.cerrar();
            }
        });
    }

    private String obtenerNombreMarcaYmodelo(){
        String nombre = "";
        BaseDatos base = new BaseDatos(this);
        base.abrir();
        CatalogoMarcas catalogoMarcas = new CatalogoMarcas(base);
        CatalogoModelos catalogoModelos = new CatalogoModelos(base);
        Modelo modelo =  catalogoModelos.obtenerModeloPorId(vehiculo.getIdModelo());
        Marca marca = catalogoMarcas.obtenerMarcaPorId(modelo.getIdMarca());
        nombre = marca!=null?marca.getNombre():"";
        nombre += modelo!=null?" "+modelo.getNombre():"";
        base.cerrar();
        return nombre;
    }

    public void mensajeErrorAlCambiarStatus(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new MsgToast(getApplicationContext(),"Ocurrió un error al intentar cambiar el status del servicio", false,
                        TipoImportanciaToast.ERROR.getId());
            }
        });
    }

    public void mensajeExitoAlCambiarStatus(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new MsgToast(getApplicationContext(),"El status del servicio se cambió exitosamente", false,
                        TipoImportanciaToast.INFO.getId());
            }
        });
    }
    public void mostrarMensajeErrorWS(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new MsgToast(getApplicationContext(), "Ocurrió un error de comunicación con el Web Service", false,
                        TipoImportanciaToast.ERROR.getId());
            }
        });
    }
}
