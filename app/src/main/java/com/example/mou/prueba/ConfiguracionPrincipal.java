package com.example.mou.prueba;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.mou.Enum.ComunicacionEnum;
import com.example.mou.Enum.TipoImportanciaToast;
import com.example.mou.Enum.TipoUsuarioEnum;
import com.example.mou.Utilerias.DialogoCargando;
import com.example.mou.Utilerias.InformacionLocal;
import com.example.mou.Utilerias.MsgToast;
import com.example.mou.Servicio.WebServ;
import com.example.mou.data.BaseDatos;
import com.example.mou.data.DestinatarioData;
import com.example.mou.data.InvitacionesData;
import com.example.mou.data.LlavesData;
import com.example.mou.data.TelefonoData;
import com.example.mou.data.UsuarioData;
import com.example.mou.data.VehiculoData;
import com.example.mou.dto.UsuarioDTO;
import com.example.mou.dto.VehiculoDTO;
import com.example.mou.model.Destinatario;
import com.example.mou.model.Invitacion;
import com.example.mou.model.Llave;
import com.example.mou.model.Telefono;
import com.example.mou.model.Usuario;
import com.example.mou.model.Vehiculo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ConfiguracionPrincipal extends ActionBarActivity implements View.OnClickListener {

    Button b1, b2;
    TextView tvFecha;
    List<VehiculoDTO> vehiculosDTO;
    UsuarioDTO usuarioDTO;
    MenuItem menuActualizar, menuInvitacion;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.configuracion_principal);
        init();
        //si hay conexion a internet y hay usuario registrado, ejecutar la actualización
        if(usuarioDTO.getUsuario()!=null) {
            DialogoCargando.mostrarDialogo(ConfiguracionPrincipal.this);
            actualizarInformacion();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_configuracionprincipal, menu);
        menuActualizar = menu.findItem(R.id.conf_refresh);
        menuInvitacion = menu.findItem(R.id.conf_inv);
        if(usuarioDTO.getUsuario()==null){
            menuActualizar.setVisible(false);
        }
        if(!hayInvitacionesPendientes()){
            menuInvitacion.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.conf_refresh) {
            DialogoCargando.mostrarDialogo(ConfiguracionPrincipal.this);
            vehiculosDTO = new ArrayList<>();
            usuarioDTO = new UsuarioDTO();
            obtenerInformacionUsuario();
            actualizarInformacion();
        }
        if (id == R.id.conf_inv) {
            startActivity(new Intent(ConfiguracionPrincipal.this, InvitacionActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    public void init() {
        b1 = (Button) findViewById(R.id.btn_Config_Personales);
        b2 = (Button) findViewById(R.id.btn_Config_Vehiculos);
        b1.setOnClickListener(this);
        b2.setOnClickListener(this);
        tvFecha = (TextView)findViewById(R.id.tv_Config_General);
        vehiculosDTO = new ArrayList<>();
        usuarioDTO = new UsuarioDTO();
        obtenerInformacionUsuario();
        if(usuarioDTO.getUsuario()==null){
            tvFecha.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.btn_Config_Personales:
                if(usuarioDTO.getUsuario()==null){
                    intent = new Intent(this, CambiarContrasenia.class);
                }else{
                    intent = new Intent(this, DatosPersonales.class);
                }
                break;
            case R.id.btn_Config_Vehiculos:
                intent = new Intent(this, ListaVehiculos.class);
                break;
        }
        if (intent != null) {
            startActivity(intent);
        }
    }

    private boolean hayInvitacionesPendientes(){
        BaseDatos base = new BaseDatos(this);
        base.abrir();
        InvitacionesData invData = new InvitacionesData(base);
        List<Invitacion> invitaciones = invData.obtenerInvitacionesPendientes();
        base.cerrar();
        return invitaciones.size()>0?true:false;
    }

    public void actualizarInformacion(){
        if (usuarioDTO.getUsuario() != null && usuarioDTO.getUsuario().getIdUsuario() != null) {
            WebServ webServ = new WebServ();
            webServ.actualizarInformacion(usuarioDTO, vehiculosDTO, new Runnable() {
                @Override
                public void run() {
                    if(validacionGeneral()) {
                        actualizarInformacionLocal();
                        actualizarFecha();
                        InformacionLocal.guardarStatusComunicacion(ConfiguracionPrincipal.this,
                                ComunicacionEnum.CON_COMUNICACION.getId());
                        DialogoCargando.ocultarDialogo();
                    }
                }
            }, new Runnable() {
                @Override
                public void run() {
                    DialogoCargando.ocultarDialogo();
                    mensajeErrorEnTransaccion();
                }
            });
        }
    }


    public void actualizarFecha() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
            InformacionLocal.guardarUltimaFechaActualizacion(ConfiguracionPrincipal.this, null);
            String fecha = InformacionLocal.obtenerUltimaFechaActualizacion(ConfiguracionPrincipal.this);
            tvFecha.setText("Última Actualización: "+fecha);
            }
        });
    }

    public void actualizarInformacionLocal(){
        BaseDatos base = new BaseDatos(this);
        base.abrir();
        UsuarioData usuarioData = new UsuarioData(base);
        usuarioData.eliminarTodo();
        usuarioData.insertarUsuario(usuarioDTO.getUsuario());

        TelefonoData telefonoData = new TelefonoData(base);
        telefonoData.eliminarTodo();
        telefonoData.insertarTelefonos(usuarioDTO.getTelefonos());

        VehiculoData vehiculoData = new VehiculoData(base);
        vehiculoData.borrarPorTipoUsuario(TipoUsuarioEnum.TITULAR.getId());
        for(VehiculoDTO vehiculoDTO: vehiculosDTO) {
            Vehiculo vehiculo = new Vehiculo();
            vehiculo.setIdVehiculo(vehiculoDTO.getId());
            vehiculo.setTitular(true);
            vehiculo.setIdUsuario(usuarioDTO.getUsuario().getIdUsuario());
            vehiculo.setTelefono(vehiculoDTO.getTelefono());
            vehiculo.setIdModelo(vehiculoDTO.getModelo().getIdModelo());
            vehiculo.setPlacas(vehiculoDTO.getPlacas());
            vehiculo.setStatus(vehiculoDTO.getStatus().getId());
            vehiculo.setTipo(vehiculoDTO.getTipo().getId());
            vehiculoData.insertarRegistro(vehiculo);
        }

        DestinatarioData destinatarioData = new DestinatarioData(base);
        destinatarioData.eliminarTodo();
        for(VehiculoDTO vehiculoDTO : vehiculosDTO){
            for(Destinatario destinatario : vehiculoDTO.getDestinatarios()){
                destinatarioData.insertarDestinatario(destinatario);
            }
        }

        LlavesData llavesData = new LlavesData(base);
        llavesData.eliminarPorTipoUsuario(TipoUsuarioEnum.TITULAR.getId());
        for(VehiculoDTO vehiculoDTO : vehiculosDTO){
            for(Llave llave : vehiculoDTO.getLlaves()){
                llavesData.insertarLlave(llave);
            }
        }
        base.cerrar();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new MsgToast(getApplicationContext(),"La información se actualizó correctamente",false, TipoImportanciaToast.INFO.getId());
            }
        });
    }

    public boolean validacionGeneral() {
        Usuario us = usuarioDTO.getUsuario();
        List<Telefono> tels = usuarioDTO.getTelefonos();
        if ((us.getIdUsuario() == null || us.getUsuario() == null || us.getContrasena() == null || us.getNombre() == null
                || us.getaPaterno() == null)) {
            mensajeError();
            return false;
        }
        if (us.getUsuario().isEmpty() || us.getContrasena().isEmpty() || us.getNombre().isEmpty() ||
                us.getaPaterno().isEmpty()) {
            mensajeError();
            return false;
        }
        for (Telefono t : tels) {
            if ((t.getIdUsuario() == null || t.getId() == null || t.getTelefono() == null)) {
                mensajeError();
                return false;
            }
            if (t.getTelefono().isEmpty()) {
                mensajeError();
                return false;
            }
        }

        for (VehiculoDTO veh : vehiculosDTO) {
            if ((veh.getId() == null || veh.getTelefono() == null || veh.getTipo() == null || veh.getLlaves() == null
                    || veh.getDestinatarios() == null || veh.getModelo() == null || veh.getPlacas() == null
                    || veh.getStatus() == null)) {
                mensajeError();
                return false;
            }
            if (veh.getTelefono().isEmpty() || veh.getTipo().getId() == null || veh.getLlaves().size() != 2 ||
                    veh.getModelo().getIdModelo() == null || veh.getPlacas().isEmpty() || veh.getStatus().getId() == null) {
                mensajeError();
                return false;
            }
            for (Llave ll : veh.getLlaves()) {
                if ((ll.getIdTipoLlave() == null || ll.getIdVehiculo() == null || ll.getLlave() == null)) {
                    mensajeError();
                    return false;
                }
                if (ll.getLlave().isEmpty()) {
                    mensajeError();
                    return false;
                }
            }
            for (Destinatario dest : veh.getDestinatarios()) {
                if ((dest.getId() == null || dest.getTelefono() == null || dest.getIdVehiculo() == null || dest.getNombre() == null
                        || dest.getaPaterno() == null)) {
                    mensajeError();
                    return false;
                }
                if (dest.getTelefono().isEmpty() || dest.getNombre().isEmpty() ||
                        dest.getaPaterno().isEmpty()) {
                    mensajeError();
                    return false;
                }
            }
        }
        return true;
    }

    public void mensajeError(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new MsgToast(getApplicationContext(),"La información no se actualizó correctamente, intente de nuevo por favor"
                , true, TipoImportanciaToast.ERROR.getId());
            }
        });
    }

    public void mensajeErrorEnTransaccion(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new MsgToast(getApplicationContext(),"Ocurrió un error de comunicación con el Web Service"
                        , true, TipoImportanciaToast.ERROR.getId());
                String fecha = InformacionLocal.obtenerUltimaFechaActualizacion(ConfiguracionPrincipal.this);
                tvFecha.setText("Última actualización: "+fecha);
                InformacionLocal.guardarStatusComunicacion(ConfiguracionPrincipal.this,ComunicacionEnum.SIN_COMUNICACION.getId());
            }
        });
    }

    public void obtenerInformacionUsuario(){
        BaseDatos base = new BaseDatos(this);
        base.abrir();
        UsuarioData usuarioData = new UsuarioData(base);
        usuarioDTO.setUsuario(usuarioData.obtenerDatosPersonales());
        base.cerrar();
    }

}// fin clase