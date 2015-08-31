package com.example.mou.prueba;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.mou.Enum.ComunicacionEnum;
import com.example.mou.Enum.StatusInvitacion;
import com.example.mou.Enum.TipoImportanciaToast;
import com.example.mou.Servicio.WebServ;
import com.example.mou.Utilerias.DialogoConfirmacion;
import com.example.mou.Utilerias.InformacionLocal;
import com.example.mou.Utilerias.MsgToast;
import com.example.mou.data.BaseDatos;
import com.example.mou.data.CatalogoMarcas;
import com.example.mou.data.CatalogoModelos;
import com.example.mou.data.InvitacionesData;
import com.example.mou.data.LlavesData;
import com.example.mou.data.VehiculoData;
import com.example.mou.model.Invitacion;
import com.example.mou.model.Llave;
import com.example.mou.model.Marca;
import com.example.mou.model.Modelo;
import com.example.mou.model.Vehiculo;

import java.util.ArrayList;
import java.util.List;


public class InvitacionActivity extends ActionBarActivity implements AdapterView.OnItemLongClickListener{

    ListView lvInv;
    List<Invitacion> invitaciones;
    TextView tvNoHay;
    Vehiculo vehiculo;
    List<Llave> llaves;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invitacion);
        lvInv = (ListView)findViewById(R.id.lv_invitaciones);
        obtenerInvitacionesPendientes();
        tvNoHay = (TextView) findViewById(R.id.tv_invitacion_nohay);
        if(invitaciones.size() > 0) {
            tvNoHay.setVisibility(View.GONE);
        }
        ArrayAdapter adaptador = new AdaptadorPersonalizado();
        lvInv.setAdapter(adaptador);
        lvInv.setOnItemLongClickListener(this);
    }

    private void obtenerInvitacionesPendientes(){
        BaseDatos base = new BaseDatos(this);
        base.abrir();
        InvitacionesData invData = new InvitacionesData(base);
        invitaciones = invData.obtenerInvitacionesPendientes();
        base.cerrar();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_invitacion, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_invitacion_borrarTodo) {
            if(invitaciones.size() > 0){
                mostrarDialogoBorrarTodo();
            }else{
                new MsgToast(this, "No hay invitaciones para borrar", false, TipoImportanciaToast.ERROR.getId());
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        final Invitacion inv = invitaciones.get(position);
        final int posicion = position;
        DialogoConfirmacion.dialogoEnviarMensaje(InvitacionActivity.this, "Confirmación",
                "¿Qué desea hacer con esta invitación?", new Runnable() {
                    @Override
                    public void run() {
                        responderInvitacion(posicion, inv.getId(),inv.getIdVehiculo(), inv.getIdDestinatario(),
                                    StatusInvitacion.RECHAZADA.getId());
                    }
                }, new Runnable() {
                    @Override
                    public void run() {
                        responderInvitacion(posicion, inv.getId(),inv.getIdVehiculo(), inv.getIdDestinatario(),
                                    StatusInvitacion.ACEPTADA.getId());
                    }
                });
        return false;
    }

    private void responderInvitacion(final Integer posicion, final Integer idInv, Integer idVehiculo, Integer idDest, final Integer statusInv){
        vehiculo = new Vehiculo();
        llaves = new ArrayList<>();
        WebServ ws = new WebServ();
        ws.aceptarOrechazarInvitacionPorWS(idVehiculo, idDest, statusInv,vehiculo, llaves, new Runnable() {
            @Override
            public void run() {
                guardarInformacionLocal(posicion, idInv);
            }
        }, new Runnable() {
            @Override
            public void run() {
                mensajeInvitacionRechazada(posicion, idInv);
            }
        }, new Runnable() {
            @Override
            public void run() {
                mensajeErrorEnTransaccion();
            }
        });
    }

    private void guardarInformacionLocal(final Integer posicion, Integer idInvitacion){
        if(vehiculo != null){
            BaseDatos base = new BaseDatos(this);
            base.abrir();
            VehiculoData vehData = new VehiculoData(base);
            int exito = vehData.insertarRegistro(vehiculo);
            if(exito > 0) {
                LlavesData llavesData = new LlavesData(base);
                for(Llave llave : llaves) {
                    llavesData.insertarLlave(llave);
                }
                InvitacionesData invDat = new InvitacionesData(base);
                invDat.eliminarInvitacionPorId(idInvitacion);
                base.cerrar();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        invitaciones.remove(posicion.intValue());
                        ArrayAdapter adaptador = new AdaptadorPersonalizado();
                        lvInv.setAdapter(adaptador);
                        new MsgToast(InvitacionActivity.this, "La invitación se respondió exitosamente", false,
                                TipoImportanciaToast.INFO.getId());
                    }
                });
            }
        }else{
            mensajeErrorLocal("Se aceptó la invitación, pero no se obtuvo el vehículo");
        }
    }

    private class AdaptadorPersonalizado extends ArrayAdapter<Invitacion> {

        public AdaptadorPersonalizado() {
            super(InvitacionActivity.this, R.layout.item_view_invitaciones, invitaciones);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //Nos aseguramos que exista una vista con la cual trabajar
            View itemView = convertView;
            if(itemView == null){
                itemView = getLayoutInflater().inflate(R.layout.item_view_invitaciones, parent, false);
            }
            Invitacion inv = invitaciones.get(position);

            //Llenamos la vista
            TextView tvTitulo = (TextView)itemView.findViewById(R.id.lv_invitacion_mensaje);
            tvTitulo.setText(obtenerNombreMarcaYmodelo(inv.getIdModelo()));
            //   return super.getView(position, convertView, parent);
            return itemView;
        }
    }

    private String obtenerNombreMarcaYmodelo(Integer idModelo){
        String nombre = "";
        BaseDatos base = new BaseDatos(InvitacionActivity.this);
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

    private void borrarTodasInvitaciones(){
        BaseDatos base = new BaseDatos(this);
        base.abrir();
        InvitacionesData invDat = new InvitacionesData(base);
        invDat.eliminarTodo();
        base.cerrar();
    }

    private void mostrarDialogoBorrarTodo(){
        try {
            DialogoConfirmacion.mostrarDialogo(this, "Cuidado!", "¿Desea borrar todas las invitaciones?","Borrar",
                    "Cancelar", new Runnable() {
                        @Override
                        public void run() {
                            borrarTodasInvitaciones();
                            invitaciones.clear();
                            ArrayAdapter adaptador = new AdaptadorPersonalizado();
                            lvInv.setAdapter(adaptador);
                            tvNoHay.setVisibility(View.VISIBLE);
                        }
                    }, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void mensajeInvitacionRechazada(final Integer posicion, final Integer idInvitacion){
      runOnUiThread(new Runnable() {
      @Override
       public void run() {
          BaseDatos base = new BaseDatos(InvitacionActivity.this);
          base.abrir();
          InvitacionesData invData = new InvitacionesData(base);
          invData.eliminarInvitacionPorId(idInvitacion);
          base.cerrar();
          invitaciones.remove(posicion.intValue());
          ArrayAdapter adaptador = new AdaptadorPersonalizado();
          lvInv.setAdapter(adaptador);
          new MsgToast(InvitacionActivity.this, "La invitación se respondió correctamente", false,
                      TipoImportanciaToast.INFO.getId());
          }
       });
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

    private void mensajeErrorLocal(final String mensaje){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new MsgToast(InvitacionActivity.this, mensaje, false,
                        TipoImportanciaToast.ERROR.getId());
            }
        });
    }

}
