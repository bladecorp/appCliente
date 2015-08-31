package com.example.mou.prueba;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.mou.Enum.ComunicacionEnum;
import com.example.mou.Enum.MensajeGeneral;
import com.example.mou.Enum.TipoImportanciaToast;
import com.example.mou.Enum.TipoMensajeEnum;
import com.example.mou.Enum.TipoStatusEnum;
import com.example.mou.Enum.TipoVehiculoEnum;
import com.example.mou.Servicio.WebServ;
import com.example.mou.Utilerias.DialogoConfirmacion;
import com.example.mou.Utilerias.InformacionLocal;
import com.example.mou.Utilerias.MsgToast;
import com.example.mou.data.BaseDatos;
import com.example.mou.data.CatalogoMarcas;
import com.example.mou.data.CatalogoModelos;
import com.example.mou.data.DestinatarioData;
import com.example.mou.data.LlavesData;
import com.example.mou.data.TipoStatusData;
import com.example.mou.data.UsuarioData;
import com.example.mou.data.VehiculoData;
import com.example.mou.dto.VehiculoDTO;
import com.example.mou.model.Destinatario;
import com.example.mou.model.Marca;
import com.example.mou.model.Modelo;
import com.example.mou.model.TipoMensaje;
import com.example.mou.model.TipoStatus;
import com.example.mou.model.Usuario;
import com.example.mou.model.Vehiculo;

import java.util.ArrayList;
import java.util.List;


public class ListaVehiculos extends ActionBarActivity implements View.OnClickListener{

    private List<Vehiculo> vehiculos = new ArrayList<>();
    private ListView lista;
    private ImageButton btnAgregarVehiculo;
    private Usuario usuario;
    private VehiculoDTO vehiculoDTO;
    private int status;
    private MenuItem btnBorrar;
    TextView tvNoHay;
    private static final String NUM_TWILIO = "+525549998734";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_vehiculos);
        init();
    }

    public void init(){
        btnAgregarVehiculo = (ImageButton)findViewById(R.id.lista_vehiculos_btn_agregar);
        btnAgregarVehiculo.setOnClickListener(this);
        lista = (ListView)findViewById(R.id.lista_vehiculos);
        tvNoHay = (TextView)findViewById(R.id.lista_vehiculos_tvNoHay);
        //  lista.setOnItemLongClickListener(this);
        registerForContextMenu(lista);
        usuario = new Usuario();
        vehiculoDTO = new VehiculoDTO();
        obtenerUsuario();
        obtenerVehiculos();
        llenarListaVehiculos();
        status = InformacionLocal.obtenerStatusComunicacion(this);
        if(status == ComunicacionEnum.SIN_COMUNICACION.getId() && usuario != null){
            new MsgToast(this, "No se podrán hacer modificaciones ya que no hay comunicación con el web service",
                    false, TipoImportanciaToast.ERROR.getId());
        }
        if(vehiculos.size()>0){tvNoHay.setVisibility(View.GONE);}

        if(usuario == null){btnAgregarVehiculo.setVisibility(View.GONE);}
    }

    private void obtenerVehiculos() {
        BaseDatos base = new BaseDatos(this);
        base.abrir();
        VehiculoData aData = new VehiculoData(base);
        vehiculos = aData.obtenerTodosVehiculos();
        base.cerrar();
    }

    private void llenarListaVehiculos() {
        ArrayAdapter adaptador = new AdaptadorPersonalizado();
        lista.setAdapter(adaptador);
        establecerTamanioListView();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_lista_vehiculos, menu);
        btnBorrar = menu.findItem(R.id.btn_lista_eliminartodo);
        if(status == ComunicacionEnum.SIN_COMUNICACION.getId()){
            btnBorrar.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id){
            case R.id.btn_lista_eliminartodo:
                mostrarDialogoEliminarTodo();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.lista_vehiculos_btn_agregar:
                Intent in = new Intent(this, Vehiculos.class);
                startActivity(in);
                break;
        }
    }

    private class AdaptadorPersonalizado extends ArrayAdapter<Vehiculo> {

        public AdaptadorPersonalizado() {
            super(ListaVehiculos.this, R.layout.item_view_vehiculos, vehiculos);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //Nos aseguramos que exista una vista con la cual trabajar
            View itemView = convertView;
            if(itemView == null){
                itemView = getLayoutInflater().inflate(R.layout.item_view_vehiculos, parent, false);
            }
            //Vehiculo seleccionado
            Vehiculo vehiculo = vehiculos.get(position);
            TipoStatus status = obtenerTiposStatus(vehiculo.getStatus());

            //Llenamos la vista
            TextView tvMarcaModelo = (TextView)itemView.findViewById(R.id.lv_lista_marcamodelo);
            TextView tvPlacas = (TextView)itemView.findViewById(R.id.lv_lista_placas);
            TextView tvStatus = (TextView)itemView.findViewById(R.id.lv_lista_status);
            ImageView imagen = (ImageView)itemView.findViewById(R.id.lv_lista_imagen);
            TextView tvTitular = (TextView)itemView.findViewById(R.id.lv_lista_titular);

            tvTitular.setText(vehiculo.isTitular()?"Titular":"Invitado");
            tvTitular.setTextColor(vehiculo.isTitular()?Color.YELLOW: Color.rgb(174,54,54));

            tvMarcaModelo.setText(obtenerNombreMarcaYmodelo(vehiculo.getIdModelo()));
            tvPlacas.setText(vehiculo.getPlacas()!=null?vehiculo.getPlacas():"N/D");

            tvStatus.setText(status.getTipo());
            tvStatus.setTextColor(vehiculo.getStatus()== TipoStatusEnum.ACTIVADO.getId()? Color.YELLOW: Color.rgb(174,54,54));

            if(vehiculo.getTipo() == TipoVehiculoEnum.AUTO.getId()){
                imagen.setImageResource(R.drawable.auto_original);
            }else {
                imagen.setImageResource(R.drawable.camioneta_original);
            }
            //   return super.getView(position, convertView, parent);
            return itemView;
        }
    }

    private TipoStatus obtenerTiposStatus(int idStatus){
        BaseDatos base = new BaseDatos(this);
        base.abrir();
        TipoStatusData status = new TipoStatusData(base);
        TipoStatus stat = status.obtenerTipoStatusPorId(idStatus);
        base.cerrar();
        return stat;
    }

    private String obtenerNombreMarcaYmodelo(Integer idModelo){
        String nombre = "";
        BaseDatos base = new BaseDatos(ListaVehiculos.this);
        base.abrir();
        CatalogoMarcas catalogoMarcas = new CatalogoMarcas(base);
        CatalogoModelos catalogoModelos = new CatalogoModelos(base);
        Modelo modelo =  catalogoModelos.obtenerModeloPorId(idModelo);
        Marca marca = catalogoMarcas.obtenerMarcaPorId(modelo.getIdMarca());
        nombre = marca!=null?marca.getNombre():"";
        nombre += modelo!=null?" "+modelo.getNombre():"";
        base.cerrar();
        return nombre;
    }

    public void eliminarRegistrosTodos(){
        BaseDatos base = new BaseDatos(this);
        base.abrir();
        LlavesData llData = new LlavesData(base);
        llData.eliminarTodo();
        DestinatarioData destData = new DestinatarioData(base);
        destData.eliminarTodo();
        VehiculoData vData = new VehiculoData(base);
        int elementosBorrados = vData.borrarTodos();
        if(elementosBorrados == 0){
            new MsgToast(this, "No había vehículos registrados", false, TipoImportanciaToast.ERROR.getId());
        }else {
            new MsgToast(this, "Se eliminaron "+elementosBorrados+" vehículos registrados", false, TipoImportanciaToast.INFO.getId());
        }
        base.cerrar();
        obtenerVehiculos();
        llenarListaVehiculos();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new MsgToast(ListaVehiculos.this, "Los vehículos se eliminaron exitosamente", false, TipoImportanciaToast.ERROR.getId());
                tvNoHay.setVisibility(View.VISIBLE);
            }
        });
    }

    private void eliminarVehiculosPorUsuario(){
        WebServ ws = new WebServ();
        ws.eliminarVehiculosPorIdUsuarioWS(usuario.getIdUsuario(),vehiculos,new Runnable() {
            @Override
            public void run() {
                eliminarRegistrosTodos();
            }
        }, new Runnable() {
            @Override
            public void run() {
                mensajeErrorAlEnviarMensaje();
            }
        }, new Runnable() {
            @Override
            public void run() {
                mensajeErrorTransacción();
            }
        });
    }

    public void establecerTamanioListView(){
        /*final float scale = this.getResources().getDisplayMetrics().density;
        int pixeles;
        if(vehiculos.size() == 0){
            pixeles = (int) (30 * scale + 0.5f);
            lista.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,pixeles));
        }else if(vehiculos.size() == 1){
            pixeles = (int) (113 * scale + 0.5f);
            lista.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,pixeles));
        }else if(vehiculos.size() == 2){
            pixeles = (int) (226 * scale + 0.5f);
            lista.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,pixeles));
        }else if (vehiculos.size() >= 3){
            pixeles = (int) (340 * scale + 0.5f);
            lista.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,pixeles));
        }*/
    }

    public void mostrarDialogoEliminarTodo(){
        try {
            DialogoConfirmacion.mostrarDialogo(this, "Cuidado!", "¿Desea eliminar todos los vehículos registrados?",
                    "Borrar", "Cancelar", new Runnable() {
                        @Override
                        public void run() {
                            eliminarVehiculosPorUsuario();
                        }
                    }, null);
        }catch(Exception ex){
            new MsgToast(this, "Error al mostrar el diálogo eliminar todo", false, TipoImportanciaToast.ERROR.getId());
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
      //  super.onCreateContextMenu(menu, v, menuInfo);
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        menu.setHeaderTitle(obtenerNombreMarcaYmodelo(vehiculos.get(info.position).getIdModelo()));
        getMenuInflater().inflate(R.menu.ctmenu_lista_vehiculos, menu);
        MenuItem menuDestinatarios = menu.findItem(R.id.ctmenu_lista_vehiculos_dest);
        MenuItem menuHistorico = menu.findItem(R.id.ctmenu_lista_vehiculos_histMensajes);
        MenuItem menuStatus = menu.findItem(R.id.ctmenu_lista_vehiculos_statusServ);
        MenuItem menuKeepA = menu.findItem(R.id.ctmenu_lista_vehiculos_keepAlive);
        if(InformacionLocal.obtenerStatusComunicacion(ListaVehiculos.this) == ComunicacionEnum.SIN_COMUNICACION.getId()){
            menuStatus.setEnabled(false);
            menuKeepA.setEnabled(false);
        }
        if(!vehiculos.get(info.position).isTitular()){
            menuDestinatarios.setVisible(false);
            menuHistorico.setVisible(false);
            menuStatus.setVisible(false);
            menuKeepA.setVisible(false);
        }

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        int posicion = info.position;
        Intent intent;
        switch(item.getItemId()){
            case R.id.ctmenu_lista_vehiculos_histMensajes:
                intent = new Intent(this, MensajesActivity.class);
                intent.putExtra("idVehiculo", vehiculos.get(posicion).getIdVehiculo());
                startActivity(intent);
                break;
            case R.id.ctmenu_lista_vehiculos_editar:
                intent = new Intent(this, Vehiculos.class);
                intent.putExtra("idVehiculo", vehiculos.get(posicion).getIdVehiculo());
                startActivity(intent);
                break;
            case R.id.ctmenu_lista_vehiculos_statusServ:
                intent = new Intent(this, StatusActivity.class);
                intent.putExtra("idVehiculo", vehiculos.get(posicion).getIdVehiculo());
                startActivity(intent);
                break;
            case R.id.ctmenu_lista_vehiculos_dest:
                intent = new Intent(this, DestinatariosActivity.class);
                intent.putExtra("idVehiculo", vehiculos.get(posicion).getIdVehiculo());
                startActivity(intent);
                break;
            case R.id.ctmenu_lista_vehiculos_keepAlive:
                mostrarMensajeRevisionSensores(vehiculos.get(posicion).getIdVehiculo());
                break;
        }
        return super.onContextItemSelected(item);
    }

    private void mostrarMensajeRevisionSensores(final Integer idVehiculo){
        DialogoConfirmacion.dialogoEnviarMensaje(ListaVehiculos.this,"Revisar Sensores","Por qué medio desea enviar la " +
                "solicitud de revisión de sensores?",new Runnable() {
            @Override
            public void run() {
                enviarRevisionSensoresPorSMS(idVehiculo);
            }
        }, new Runnable() {
            @Override
            public void run() {
                enviarRevisionSensoresPorWS(idVehiculo);
            }
        });
    }

    private void enviarRevisionSensoresPorSMS(Integer idVehiculo){
        String mensaje = TipoMensajeEnum.GENERAL.getId()+";"+ MensajeGeneral.REVISAR_SENSORES.getId()+";"
                +idVehiculo;
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(NUM_TWILIO, null, mensaje, null, null);
        new MsgToast(getApplicationContext(),"Mensaje enviado exitosamente, en breve podrá visualizar el reporte"
                , false, TipoImportanciaToast.INFO.getId());
    }

    private void enviarRevisionSensoresPorWS(Integer idVehiculo){
        WebServ ws = new WebServ();
        ws.enviarRevisionSensoresPorWS(idVehiculo,new Runnable() {
            @Override
            public void run() {
                mensajeExitoEnviar();
            }
        }, new Runnable() {
            @Override
            public void run() {
                mensajeErrorAlEnviarMensaje();
            }
        }, new Runnable() {
            @Override
            public void run() {
                mensajeErrorTransacción();
            }
        });
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        Log.d("DEBUG","Entró a OnPostResume");
        obtenerVehiculos();
        llenarListaVehiculos();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public void obtenerUsuario(){
        BaseDatos base = new BaseDatos(this);
        base.abrir();
        UsuarioData usData = new UsuarioData(base);
        usuario = usData.obtenerDatosPersonales();
        base.cerrar();
    }

    private void mensajeExitoEnviar(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new MsgToast(ListaVehiculos.this, "El mensaje se envió exitosamente, en breve recibirá el reporte en su celular", true,
                        TipoImportanciaToast.INFO.getId());
            }
        });
    }

    private void mensajeErrorTransacción(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new MsgToast(ListaVehiculos.this, "Ocurrió un error de comunicación con el Web Service", false,
                        TipoImportanciaToast.ERROR.getId());
            }
        });
    }
    private void mensajeErrorAlEnviarMensaje(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new MsgToast(ListaVehiculos.this, "No se pudo enviar el mensaje, intente de nuevo por favor", false,
                        TipoImportanciaToast.ERROR.getId());
            }
        });
    }

    private void mensajeErrorAlEliminar(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new MsgToast(ListaVehiculos.this, "No se pudieron eliminar los vehículos, intente más tarde", false,
                        TipoImportanciaToast.ERROR.getId());
            }
        });
    }

}
