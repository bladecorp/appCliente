package com.example.mou.prueba;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsManager;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.mou.Enum.ComunicacionEnum;
import com.example.mou.Enum.MensajeGeneral;
import com.example.mou.Enum.StatusInvitacion;
import com.example.mou.Enum.TipoImportanciaToast;
import com.example.mou.Enum.TipoMensajeEnum;
import com.example.mou.Servicio.WebServ;
import com.example.mou.Utilerias.DialogoCargando;
import com.example.mou.Utilerias.DialogoConfirmacion;
import com.example.mou.Utilerias.InformacionLocal;
import com.example.mou.Utilerias.MsgToast;
import com.example.mou.data.BaseDatos;
import com.example.mou.data.CatalogoMarcas;
import com.example.mou.data.CatalogoModelos;
import com.example.mou.data.DestinatarioData;
import com.example.mou.data.UsuarioData;
import com.example.mou.data.VehiculoData;
import com.example.mou.model.Destinatario;
import com.example.mou.model.Marca;
import com.example.mou.model.Modelo;
import com.example.mou.model.Usuario;
import com.example.mou.model.Vehiculo;

import java.util.ArrayList;
import java.util.List;


public class DestinatariosActivity extends ActionBarActivity implements View.OnClickListener{

    Integer idVehiculo;
    Vehiculo vehiculo;
    List<Destinatario> destinatarios;
    List<Destinatario> listaDestinatarios;
    Usuario usuario;
    ListView lvDest;
    ImageButton btnAgregar;
    TextView tvVehiculo, tvPlacas, tvNoHay;
    ArrayAdapter adaptador;
    MenuItem itemBorrarTodo;
    private static final String NUM_TWILIO = "+525549998734";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_destinatarios);
        init();
    }

    public void init(){
        lvDest = (ListView)findViewById(R.id.lista_destinatarios);
        btnAgregar = (ImageButton)findViewById(R.id.lista_destinatarios_btn_agregar);
        btnAgregar.setOnClickListener(this);
        registerForContextMenu(lvDest);
        tvNoHay = (TextView)findViewById(R.id.lista_destinatarios_tvNoHay);
        tvVehiculo = (TextView)findViewById(R.id.lista_destinatarios_tvVehiculo);
        tvPlacas = (TextView)findViewById(R.id.lista_destinatarios_tvPlacas);
        idVehiculo = getIntent().getIntExtra("idVehiculo",-1);
        obtenerUsuario();
        obtenerVehiculo();
        obtenerDestinatarios();
        adaptador = new AdaptadorPersonalizado();
        lvDest.setAdapter(adaptador);
        tvVehiculo.setText(obtenerNombreMarcaYmodelo());
        tvPlacas.setText(vehiculo.getPlacas());
        if(listaDestinatarios.size() > 0){
            tvNoHay.setVisibility(View.GONE);
        }
    }

    private void mostrarConfirmacionEliminar(final Destinatario dest, final int posicion){
        try {
            DialogoConfirmacion.mostrarDialogo(DestinatariosActivity.this,"Confirmación", "¿Desea eliminar a este destinatario?\n\n" +
                    dest.getNombre()+" "+dest.getaPaterno()+"\n" +
                    dest.getTelefono(),"Eliminar", "Cancelar",new Runnable() {
                @Override
                public void run() {
                    eliminarDestinatarioEnWS(dest.getId(), dest.getIdVehiculo(), posicion);
                }
            },null);
        } catch (Exception e) {
            new MsgToast(getApplicationContext(),"Error al mostrar el diálogo",false,TipoImportanciaToast.ERROR.getId());
        }
    }

    private void eliminarDestinatarioEnWS(Integer idDestinatario, Integer idVehiculo, final Integer posicion){
        DialogoCargando.mostrarDialogo(DestinatariosActivity.this);
        WebServ ws = new WebServ();
        destinatarios = new ArrayList<>();
        ws.eliminarDestinatarioEnWS(idDestinatario, idVehiculo, destinatarios,new Runnable() {
            @Override
            public void run() {
                if(validarDatosWS()) {
                    guardarInformacionLocal();
                    obtenerDestinatarios();
                    DialogoCargando.ocultarDialogo();
                    recargarLista();
                }else{
                    DialogoCargando.ocultarDialogo();
                    mensajeInformacionIncompleta();
                }
            }
        }, new Runnable() {
            @Override
            public void run() {
                DialogoCargando.ocultarDialogo();
                mensajeErrorAlEliminar(false);
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
                BaseDatos base = new BaseDatos(DestinatariosActivity.this);
                base.abrir();
                DestinatarioData destData = new DestinatarioData(base);
                destData.eliminarDestinatarioPorIdVehiculo(idVehiculo);
                for(Destinatario dest : destinatarios) {
                    destData.insertarDestinatario(dest);
                }
                base.cerrar();
                new MsgToast(getApplicationContext(), "EL destinatario se eliminó exitosamente", false,
                            TipoImportanciaToast.INFO.getId());
            }
        });
    }

    private boolean validarDatosWS(){
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_destinatarios, menu);
        if(InformacionLocal.obtenerStatusComunicacion(DestinatariosActivity.this) == ComunicacionEnum.SIN_COMUNICACION.getId()){
            itemBorrarTodo = menu.findItem(R.id.menu_dest_borrarTodo);
            itemBorrarTodo.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_dest_borrarTodo) {
            if(listaDestinatarios.size() > 0){
                mostrarConfirmacionEliminarTodos();
            }else{
                new MsgToast(this, "No hay destinatarios para eliminar", false, TipoImportanciaToast.ERROR.getId());
            }
        }
        if(id == R.id.menu_dest_info){
            try {
                DialogoConfirmacion.infoStatusInvitacion(DestinatariosActivity.this, null);
            } catch (Exception e) {
                new MsgToast(this, "Error al mostrar el diálogo", false, TipoImportanciaToast.ERROR.getId());
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        //  super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("OPCIONES ADICIONALES");
        getMenuInflater().inflate(R.menu.ctmenu_lista_destinatarios, menu);
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        if(InformacionLocal.obtenerStatusComunicacion(DestinatariosActivity.this) == ComunicacionEnum.SIN_COMUNICACION.getId()){
            MenuItem itemB = menu.findItem(R.id.ctmenu_lista_dest_borrar);
            MenuItem itemE = menu.findItem(R.id.ctmenu_lista_dest_editar);
            MenuItem itemI = menu.findItem(R.id.ctmenu_lista_dest_enviarInv);
            itemB.setEnabled(false);
            itemE.setEnabled(false);
            itemI.setEnabled(false);
        }

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        int posicion = info.position;
        Intent i;
        switch(item.getItemId()){
            case R.id.ctmenu_lista_dest_editar:
                i = new Intent(DestinatariosActivity.this,AgregarDestActivity.class);
                i.putExtra("idDestinatario", listaDestinatarios.get(posicion).getId());
                i.putExtra("idVehiculo", idVehiculo);
                startActivityForResult(i, 2404);
                break;
            case R.id.ctmenu_lista_dest_borrar:
                mostrarConfirmacionEliminar(listaDestinatarios.get(posicion), posicion);
                break;
            case R.id.ctmenu_lista_dest_enviarInv:
                mostrarDialogoEnvioInvitacion(listaDestinatarios.get(posicion).getId());
                break;
        }
        return true;
        //  return super.onContextItemSelected(item);
    }

    public void obtenerUsuario(){
        BaseDatos base = new BaseDatos(this);
        base.abrir();
        UsuarioData usData = new UsuarioData(base);
        usuario = usData.obtenerDatosPersonales();
        base.cerrar();
    }

    public void obtenerDestinatarios(){
        BaseDatos base = new BaseDatos(this);
        base.abrir();
        DestinatarioData destData = new DestinatarioData(base);
        listaDestinatarios = destData.obtenerDestinatariosPorIdVehiculo(idVehiculo);
        base.cerrar();
    }

    public void obtenerVehiculo(){
        BaseDatos base = new BaseDatos(this);
        base.abrir();
        VehiculoData vehData = new VehiculoData(base);
        vehiculo = vehData.obtenerVehiculoPorId(idVehiculo);
        base.cerrar();
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.lista_destinatarios_btn_agregar:
                Intent i = new Intent(DestinatariosActivity.this,AgregarDestActivity.class);
                i.putExtra("idVehiculo", idVehiculo);
                startActivityForResult(i, 2404);
                break;
        }
    }

    private void eliminarTodosDestinatarios(){

            WebServ ws = new WebServ();
            ws.eliminarTodosDestinatariosEnWS(idVehiculo, new Runnable() {
                @Override
                public void run() {
                    DialogoCargando.ocultarDialogo();
                    borrarDestinatariosLocal();
                    listaDestinatarios.clear();
                    recargarLista();
                }
            }, new Runnable() {
                @Override
                public void run() {
                    DialogoCargando.ocultarDialogo();
                    mensajeErrorAlEliminar(true);
                }
            }, new Runnable() {
                @Override
                public void run() {
                    DialogoCargando.ocultarDialogo();
                    mensajeErrorEnTransaccion();
                }
            });
    }

    private void borrarDestinatariosLocal(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                BaseDatos base = new BaseDatos(DestinatariosActivity.this);
                DestinatarioData destData = new DestinatarioData(base);
                int res = destData.eliminarDestinatarioPorIdVehiculo(idVehiculo);
                new MsgToast(DestinatariosActivity.this, "Los destinatarios se eliminaron exitosamente", false, TipoImportanciaToast.INFO.getId());
            }
        });
    }

    private void mostrarDialogoEnvioInvitacion(final Integer idDestinatario){
        try{
            DialogoConfirmacion.dialogoEnviarMensaje(DestinatariosActivity.this, "ELIJA UNA OPCIÓN",
                    "¿Por qué medio desea enviar la invitación?", new Runnable() {
                        @Override
                        public void run() {
                            enviarMensajePorSMS(idDestinatario);
                        }
                    } , new Runnable() {
                        @Override
                        public void run() {
                            DialogoCargando.mostrarDialogo(DestinatariosActivity.this);
                            enviarMensajePorWS(idDestinatario);
                        }
                    });
        }catch(Exception ex){
            new MsgToast(this, "Error al mostrar el diálogo", false, TipoImportanciaToast.ERROR.getId());
        }
    }

    private void enviarMensajePorSMS(Integer idDestinatario){
        String mensaje = TipoMensajeEnum.GENERAL.getId()+";"+ MensajeGeneral.INVITACION.getId()+";"
                +idVehiculo+";"+idDestinatario;
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(NUM_TWILIO, null, mensaje, null, null);
        cambiarStatusInvitacion(idDestinatario);
        for(Destinatario d : listaDestinatarios){
            if(d.getId() == idDestinatario){
                d.setStatusInv(StatusInvitacion.ENVIADA.getId());
            }
        }
        recargarLista();
    }

    private void enviarMensajePorWS(final Integer idDestinatario){
        WebServ ws = new WebServ();
        ws.enviarInvitacionPorWS(idVehiculo, idDestinatario, new Runnable() {
            @Override
            public void run() {
                DialogoCargando.ocultarDialogo();
                cambiarStatusInvitacion(idDestinatario);
                for(Destinatario d : listaDestinatarios){
                    if(d.getId() == idDestinatario){
                        d.setStatusInv(StatusInvitacion.ENVIADA.getId());
                    }
                }
                recargarLista();
            }
        }, new Runnable() {
            @Override
            public void run() {
                DialogoCargando.ocultarDialogo();
                mensajeErrorEnEnviarInvitacion();
            }
        }, new Runnable() {
            @Override
            public void run() {
                DialogoCargando.ocultarDialogo();
                mensajeErrorEnTransaccion();
            }
        });
    }

    private void cambiarStatusInvitacion(final Integer idDestinatario){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                BaseDatos base = new BaseDatos(DestinatariosActivity.this);
                base.abrir();
                DestinatarioData destData = new DestinatarioData(base);
                boolean exito = destData.actualizarStatusInvitacion(idDestinatario, StatusInvitacion.ENVIADA.getId());
                base.cerrar();
                new MsgToast(DestinatariosActivity.this, "La invitación fue enviada exitosamente", false,
                            TipoImportanciaToast.INFO.getId());
            }
        });
    }

    private void mensajeErrorAlEliminar(final boolean todos){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String mensaje = todos?"No se pudieron eliminar los destinatarios":"No se pudo eliminar el destinatario";
                mensaje.concat(", intente de nuevo por favor");
                new MsgToast(getApplicationContext(), mensaje,
                        true, TipoImportanciaToast.ERROR.getId());
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

    private void mensajeErrorEnEnviarInvitacion(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new MsgToast(getApplicationContext(), "No se pudo enviar la invitación, intente de nuevo por favor",
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

    private void recargarLista(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adaptador = new AdaptadorPersonalizado();
                lvDest.setAdapter(adaptador);
                tvNoHay.setVisibility(listaDestinatarios.size()>0?View.GONE:View.VISIBLE);
            }
        });
    }

    private void mostrarConfirmacionEliminarTodos(){
        try {
            DialogoConfirmacion.mostrarDialogo(this, "Confirmación", "¿Desea eliminar a todos los destinatarios de este vehículo?",
                    "Confirmar", "Cancelar", new Runnable() {
                        @Override
                        public void run() {
                            DialogoCargando.mostrarDialogo(DestinatariosActivity.this);
                            eliminarTodosDestinatarios();
                        }
                    }, null);
        } catch (Exception e) {
            new MsgToast(this, "Error al mostrar el diálogo", false, TipoImportanciaToast.ERROR.getId());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == 2404) {
            if(data != null) {
                idVehiculo = data.getIntExtra("idVehiculo",-1);
            }
        }
    }

    private class AdaptadorPersonalizado extends ArrayAdapter<Destinatario> {

        public AdaptadorPersonalizado() {
            super(DestinatariosActivity.this, R.layout.item_view_destinatarios, listaDestinatarios);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //Nos aseguramos que exista una vista con la cual trabajar
            View itemView = convertView;
            if(itemView == null){
                itemView = getLayoutInflater().inflate(R.layout.item_view_destinatarios, parent, false);
            }
            //Vehiculo seleccionado
            Destinatario dest = listaDestinatarios.get(position);

            //Llenamos la vista
            TextView tvNombreDest = (TextView)itemView.findViewById(R.id.lv_dest_nombre);
            TextView tvTelefono = (TextView)itemView.findViewById(R.id.lv_dest_telefono);
            ImageView imgStatus = (ImageView)itemView.findViewById(R.id.lv_dest_img);
            if(dest.getStatusInv() == StatusInvitacion.NO_ENVIADA.getId()) {
                imgStatus.setImageResource(android.R.drawable.ic_dialog_email);
            }else if(dest.getStatusInv() == StatusInvitacion.ENVIADA.getId()) {
                imgStatus.setImageResource(android.R.drawable.ic_menu_send);
            }else if(dest.getStatusInv() == StatusInvitacion.RECHAZADA.getId()){
                imgStatus.setImageResource(android.R.drawable.ic_delete);
            }else{
                imgStatus.setImageResource(android.R.drawable.checkbox_on_background);
            }
            tvNombreDest.setText(dest.getNombre()+" "+dest.getaPaterno());
            tvTelefono.setText(PhoneNumberUtils.formatNumber(dest.getTelefono()));
            //Color.YELLOW: Color.rgb(174,54,54)
            //   return super.getView(position, convertView, parent);
            return itemView;
        }
    }



}
