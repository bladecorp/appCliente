package com.example.mou.prueba;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.mou.Enum.ComunicacionEnum;
import com.example.mou.Enum.MensajeGeneral;
import com.example.mou.Enum.TipoImportanciaToast;
import com.example.mou.Enum.TipoMensajeEnum;
import com.example.mou.Enum.TipoUsuarioEnum;
import com.example.mou.Servicio.WebServ;
import com.example.mou.Utilerias.DialogoCargando;
import com.example.mou.Utilerias.DialogoConfirmacion;
import com.example.mou.Utilerias.InformacionLocal;
import com.example.mou.Utilerias.MsgToast;
import com.example.mou.data.BaseDatos;
import com.example.mou.data.CatalogoMarcas;
import com.example.mou.data.CatalogoModelos;
import com.example.mou.data.HistoricoMensajeData;
import com.example.mou.data.VehiculoData;
import com.example.mou.model.HistoricoMensajes;
import com.example.mou.model.Marca;
import com.example.mou.model.Modelo;
import com.example.mou.model.TipoMensaje;
import com.example.mou.model.Vehiculo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class MensajesActivity extends ActionBarActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener,
        AdapterView.OnItemClickListener {

    private static final Integer ENTRADA = 1;
    private static final Integer SALIDA = 2;

    MenuItem itemFiltro, itemTodos;
    TextView tvTitulo, tvNoHay;
    Spinner spinnerMes, spinnerAnio;
    String[] spinnerArrayMes = {"Enero","Febrero","Marzo","Abril","Mayo","Junio","Julio","Agosto","Septiembre",
                                "Octubre","Noviembre","Diciembre"};
    String[] spinnerArrayAnio;
    ListView lvMensajes;
    ArrayAdapter adaptador;
    Button btnBuscar;
    List<HistoricoMensajes> historicos;
    List<HistoricoMensajes> historicosFiltrado;
    Integer idVehiculo;
    Vehiculo vehiculo;
    Integer mes, anio, filtro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mensajes);
        init();
    }

    private void init(){
        tvTitulo = (TextView)findViewById(R.id.tvTitulo_mensajes);
        tvNoHay = (TextView)findViewById(R.id.tvNohay_mensajes);
        spinnerMes = (Spinner)findViewById(R.id.spinner_mensajes_mes);
        spinnerMes.setOnItemSelectedListener(this);
        spinnerAnio = (Spinner)findViewById(R.id.spinner_mensajes_anio);
        spinnerAnio.setOnItemSelectedListener(this);
        lvMensajes = (ListView)findViewById(R.id.lv_mensajes);
        btnBuscar = (Button)findViewById(R.id.btn_mensajes);
        btnBuscar.setOnClickListener(this);
        lvMensajes.setOnItemClickListener(this);
        idVehiculo = getIntent().getIntExtra("idVehiculo",-1);
        obtenerVehiculo();
        obtenerMensajes();
        Calendar cal = Calendar.getInstance();
        Integer anioAux = cal.get(Calendar.YEAR);
        spinnerArrayAnio = new String[3];
        spinnerArrayAnio[0] = anioAux.toString(); spinnerArrayAnio[1] = String.valueOf(anioAux-1);spinnerArrayAnio[2] = String.valueOf(anioAux - 2);
        ArrayAdapter<String> adaptadorMes = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, spinnerArrayMes);
        spinnerMes.setAdapter(adaptadorMes);
        ArrayAdapter<String> adaptadorAnio = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, spinnerArrayAnio);
        spinnerAnio.setAdapter(adaptadorAnio);
        if(vehiculo != null){
            tvNoHay.setVisibility(View.GONE);
            tvTitulo.setText(obtenerNombreMarcaYmodelo(vehiculo.getIdModelo()));
        }
        if(InformacionLocal.obtenerStatusComunicacion(this) == ComunicacionEnum.SIN_COMUNICACION.getId()){
            btnBuscar.setVisibility(View.GONE);
            spinnerMes.setEnabled(false);
            spinnerAnio.setEnabled(false);
        }
        spinnerMes.setSelection(0);
        spinnerAnio.setSelection(0);
        mes = 1;
        anio = 2015;
        filtro = ENTRADA;
    }

    private void obtenerMensajes(){
        if(idVehiculo != -1){
            BaseDatos base = new BaseDatos(this);
            base.abrir();
            HistoricoMensajeData hist = new HistoricoMensajeData(base);
            historicos = hist.obtenerMensajesPorIdVehiculo(idVehiculo);
            base.cerrar();
        }
    }

    private void obtenerVehiculo(){
        if(idVehiculo != -1){
            BaseDatos base = new BaseDatos(this);
            base.abrir();
            VehiculoData vehData = new VehiculoData(base);
            vehiculo = vehData.obtenerVehiculoPorId(idVehiculo);
            base.cerrar();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_mensajes:
                if(obtenerFecha()){
                    DialogoCargando.mostrarDialogo(this);
                    historicos.clear();
                    obtenerHistoricosPorWS();
                }
                break;
        }
    }

    private void obtenerHistoricosPorWS(){
        WebServ ws = new WebServ();
        ws.obtenerHistoricoMensajesPorWS(idVehiculo, mes, anio, historicos, new Runnable() {
            @Override
            public void run() {
                copiarListaCompleta();
                recargarLista();
                DialogoCargando.ocultarDialogo();
            }
        }, new Runnable() {
            @Override
            public void run() {
                DialogoCargando.ocultarDialogo();
                mostrarMensajeErrorWS();
            }
        });
    }

    private void recargarLista(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                listaGeneral();
                tvNoHay.setVisibility(historicos.size()>0?View.GONE:View.VISIBLE);
            }
        });
    }

    private void guardarInformacionLocal(){
        BaseDatos base = new BaseDatos(MensajesActivity.this);
        base.abrir();
        HistoricoMensajeData hist = new HistoricoMensajeData(base);
        hist.eliminarPorTipoUsuarioYvehiculo(TipoUsuarioEnum.TITULAR.getId(),idVehiculo);

        base.cerrar();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch(parent.getId()){
            case R.id.spinner_mensajes_mes:
                mes = position+1;
                break;
            case R.id.spinner_mensajes_anio:
                anio = Integer.parseInt(spinnerArrayAnio[position]);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_mensajes, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_mensajes_filtro) {
            if(filtro == ENTRADA){
                filtrarPorTipo(SALIDA);
                filtro = SALIDA;
            }else{
                filtrarPorTipo(ENTRADA);
                filtro = ENTRADA;
            }
            tvNoHay.setVisibility(historicosFiltrado.size() == 0?View.VISIBLE:View.GONE);
        }
        if(id == R.id.menu_mensajes_todos){
            copiarListaCompleta();
            recargarLista();
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean obtenerFecha(){
        Calendar cal = Calendar.getInstance();
        int mesAux = cal.get(Calendar.MONTH)+1;
        int anioAux = cal.get(Calendar.YEAR);
        if(anio == anioAux && mes > mesAux){
            new MsgToast(this, "No se pueden obtener resultados de fechas futuras", false, TipoImportanciaToast.ERROR.getId());
            return false;
        }
        return true;
    }

    public void mostrarMensajeErrorWS(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new MsgToast(MensajesActivity.this, "Ocurrió un error de comunicación con el Web Service", false,
                        TipoImportanciaToast.ERROR.getId());
            }
        });
    }

    private void filtrarPorTipo(Integer tipo){
        historicosFiltrado = new ArrayList<>();
        if(tipo == SALIDA) {
            for (HistoricoMensajes hist : historicos) {
                if (hist.getIdSubMensaje() == MensajeGeneral.REVISAR_SENSORES.getId()
                        || hist.getIdSubMensaje() == MensajeGeneral.INVITACION.getId()) {
                    historicosFiltrado.add(hist);
                }
            }
        }else if(tipo == ENTRADA){
            for (HistoricoMensajes hist : historicos) {
                if (hist.getIdTipoMensaje() == TipoMensajeEnum.ALARMA.getId() ||
                        hist.getIdSubMensaje() == MensajeGeneral.INFORMATIVO.getId()) {
                    historicosFiltrado.add(hist);
                }
            }
        }
        adaptador = new AdaptadorPersonalizado();
        lvMensajes.setAdapter(adaptador);
    }

    private void listaGeneral(){
        adaptador = new AdaptadorPersonalizado();
        lvMensajes.setAdapter(adaptador);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        HistoricoMensajes hist = historicosFiltrado.get(position);
        String mensaje;
        if(hist.getIdTipoMensaje() == TipoMensajeEnum.ALARMA.getId() || hist.getIdSubMensaje() == MensajeGeneral.INFORMATIVO.getId()){
            mensaje = "RECIBIDO: ";
        }else{
            mensaje = "ENVIADO: ";
        }
        mensaje += hist.getFecha()+"\n\nMENSAJE: "+hist.getMensaje();
        DialogoConfirmacion.dialogoInformativo(this, "Contenido", mensaje);
    }

    private class AdaptadorPersonalizado extends ArrayAdapter<HistoricoMensajes> {

        public AdaptadorPersonalizado() {
            super(MensajesActivity.this, R.layout.item_view_mensajes, historicosFiltrado);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //Nos aseguramos que exista una vista con la cual trabajar
            View itemView = convertView;
            if(itemView == null){
                itemView = getLayoutInflater().inflate(R.layout.item_view_mensajes, parent, false);
            }
            HistoricoMensajes hist = historicosFiltrado.get(position);

            //Llenamos la vista
            TextView tvTitulo = (TextView)itemView.findViewById(R.id.lv_mensajes_titulo);
            TextView tvMensaje = (TextView)itemView.findViewById(R.id.lv_mensajes_mensaje);
            if(hist.getIdSubMensaje() == MensajeGeneral.INVITACION.getId() || hist.getIdSubMensaje() ==
                    MensajeGeneral.REVISAR_SENSORES.getId()){
                tvTitulo.setText("Enviado: "+hist.getFecha());
            }else{
                tvTitulo.setText("Recibido: "+hist.getFecha());
            }
            String mensaje = hist.getMensaje().length() > 34?hist.getMensaje().substring(0,34)+"...":hist.getMensaje();
            tvMensaje.setText(mensaje);

            if(hist.getIdSubMensaje() == MensajeGeneral.INVITACION.getId() || hist.getIdSubMensaje() ==
                                                                            MensajeGeneral.REVISAR_SENSORES.getId()){
               itemView.setBackgroundResource(R.drawable.lv_mensajes_salida);
            }else {
                itemView.setBackgroundResource(R.drawable.lv_mensajes_entrada);
            }
            //   return super.getView(position, convertView, parent);
            return itemView;
        }
    }

    private String obtenerNombreMarcaYmodelo(Integer idModelo){
        String nombre = "";
        BaseDatos base = new BaseDatos(MensajesActivity.this);
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

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void copiarListaCompleta(){
        historicosFiltrado = new ArrayList<>();
        for(HistoricoMensajes histor : historicos){
            HistoricoMensajes h = new HistoricoMensajes();
            h.setId(histor.getId());
            h.setTipoUsuario(histor.getTipoUsuario());
            h.setIdVehiculo(histor.getIdVehiculo());
            h.setMensaje(histor.getMensaje());
            h.setIdTipoMensaje(histor.getIdTipoMensaje());
            h.setFecha(histor.getFecha());
            h.setIdSubMensaje(histor.getIdSubMensaje());
            historicosFiltrado.add(h);
        }
    }
}
