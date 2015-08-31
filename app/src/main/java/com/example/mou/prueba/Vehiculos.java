package com.example.mou.prueba;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ToggleButton;

import com.example.mou.Enum.ComunicacionEnum;
import com.example.mou.Enum.TipoImportanciaToast;
import com.example.mou.Enum.TipoLlaveEnum;
import com.example.mou.Enum.TipoStatusEnum;
import com.example.mou.Enum.TipoUsuarioEnum;
import com.example.mou.Enum.TipoVehiculoEnum;
import com.example.mou.Servicio.WebServ;
import com.example.mou.Utilerias.DialogoCargando;
import com.example.mou.Utilerias.DialogoConfirmacion;
import com.example.mou.Utilerias.InformacionLocal;
import com.example.mou.Utilerias.MsgToast;
import com.example.mou.Utilerias.ValidarTelefono;
import com.example.mou.data.BaseDatos;
import com.example.mou.data.DestinatarioData;
import com.example.mou.data.LlavesData;
import com.example.mou.data.TipoStatusData;
import com.example.mou.data.TipoVehiculoData;
import com.example.mou.data.UsuarioData;
import com.example.mou.data.VehiculoData;
import com.example.mou.data.CatalogoMarcas;
import com.example.mou.data.CatalogoModelos;
import com.example.mou.dto.VehiculoDTO;
import com.example.mou.model.Destinatario;
import com.example.mou.model.Llave;
import com.example.mou.model.TipoLlave;
import com.example.mou.model.TipoStatus;
import com.example.mou.model.TipoVehiculo;
import com.example.mou.model.Usuario;
import com.example.mou.model.Vehiculo;
import com.example.mou.model.Marca;
import com.example.mou.model.Modelo;

import java.util.ArrayList;
import java.util.List;


public class Vehiculos extends ActionBarActivity implements AdapterView.OnItemSelectedListener,
                CompoundButton.OnCheckedChangeListener{

    private Spinner spinnerMarca, spinnerModelo;
    private List<Marca> marcas = new ArrayList<Marca>();
    private List<Modelo> modelos = new ArrayList<Modelo>();
    private Marca marca;
    private Modelo modelo;
    private int cuentaInicialSpinner = 0;
    private int cantidadSpinners;
    private ToggleButton tglAuto, tglCamioneta;
    private EditText etPlacas, etCodigo, etCodigoInv, etTelefono;
    private Integer idVehiculo;

    private Usuario usuario;
    private List<VehiculoDTO> vehiculos;
    private VehiculoDTO vehiculoDTO;
    private Vehiculo vehiculo;
    private MenuItem menuBorrar, menuGuardar;
    private Integer status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehiculos);
        init();
        obtenerUsuario();
    }

    private void init() {
        spinnerMarca = (Spinner)findViewById(R.id.spinner_Marca);
        spinnerModelo = (Spinner)findViewById(R.id.spinner_Modelo);
        tglAuto = (ToggleButton)findViewById(R.id.tgl_vehiculos_auto);
        tglCamioneta = (ToggleButton)findViewById(R.id.tgl_vehiculos_camioneta);
        tglAuto.setOnCheckedChangeListener(this);
        tglCamioneta.setOnCheckedChangeListener(this);
        etPlacas = (EditText) findViewById(R.id.et_vehiculos_placas);
        etCodigo = (EditText)findViewById(R.id.et_vehiculos_codigo);
        etCodigoInv = (EditText)findViewById(R.id.et_vehiculos_codigoInv);
        etTelefono = (EditText)findViewById(R.id.et_vehiculos_telefono);
        etTelefono.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        spinnerMarca.setOnItemSelectedListener(this);
        spinnerModelo.setOnItemSelectedListener(this);
        cantidadSpinners = 2;

        obtenerMarcas();
        obtenerModelosPorMarca(1);
        ArrayAdapter<Marca> adaptadorMarca = new ArrayAdapter<Marca>(this, android.R.layout.simple_list_item_1, marcas);
        spinnerMarca.setAdapter(adaptadorMarca);

        ArrayAdapter<Modelo> adaptadorModelo = new ArrayAdapter<Modelo>(this, android.R.layout.simple_list_item_1, modelos);
        spinnerModelo.setAdapter(adaptadorModelo);


        idVehiculo = getIntent().getIntExtra("idVehiculo", -1);
        if(idVehiculo != -1){
            mostrarDatosParaEditar(idVehiculo);
        }else{
            spinnerModelo.setSelection(1,false);
            vehiculo = new Vehiculo();
        }
        vehiculoDTO = new VehiculoDTO();
        vehiculos = new ArrayList<>();

        status = InformacionLocal.obtenerStatusComunicacion(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_vehiculos, menu);
        menuBorrar = menu.findItem(R.id.btn_menu_vehiculos_borrar);
        menuGuardar = menu.findItem(R.id.btn_menu_vehiculos_guardar);
        if(status == ComunicacionEnum.SIN_COMUNICACION.getId()){
            menuBorrar.setVisible(false);
            menuGuardar.setVisible(false);
        }
        if(usuario == null || (vehiculo.getIdVehiculo()!=null && !vehiculo.isTitular())){
            menuGuardar.setVisible(false);
            menuBorrar.setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.btn_menu_vehiculos_guardar){
            if(validarDatos()) {
                if(idVehiculo == -1) {
                    mostrarConfirmacion();
                }else{
                    mostrarConfirmacionEdicion();
                }
            }
        }
        if(id == R.id.btn_menu_vehiculos_borrar){
            if(vehiculo.getIdVehiculo()!=null) {
                mostrarConfirmacionBorrar();
            }else{
                new MsgToast(this, "No hay vehículo", false, TipoImportanciaToast.ERROR.getId());
            }
        }


        return super.onOptionsItemSelected(item);
    }

    public List<Marca> obtenerMarcas(){
        BaseDatos base = new BaseDatos(this);
        base.abrir();
        CatalogoMarcas cMarcas = new CatalogoMarcas(base);
        marcas = cMarcas.obtenerMarcas();
        base.cerrar();
        return marcas;
    }

    public void obtenerModelosPorMarca(Integer idMarca){
        BaseDatos base = new BaseDatos(this);
        base.abrir();
        CatalogoModelos cModelos = new CatalogoModelos(base);
        modelos = cModelos.obtenerModelosPorMarca(idMarca);
        base.cerrar();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        int i = 0;
        switch (parent.getId()){
            case R.id.spinner_Marca:
                i++;
                Log.d("DEBUG2  ", "Posicion = "+position+", ID = "+id);
                marca = marcas.get(position);
                obtenerModelosPorMarca(marca.getIdMarca());
                ArrayAdapter<Modelo> adaptador = new ArrayAdapter<Modelo>(this, android.R.layout.simple_list_item_1, modelos);
                spinnerModelo.setAdapter(adaptador);
                Log.d("DEBUG","Vueltas = "+i);
                break;
            case R.id.spinner_Modelo:
                i++;
                Log.d("DEBUG","Vueltas = "+i);
                modelo = modelos.get(position);
                break;
        }



    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(isChecked) {
            if (buttonView.getId() == R.id.tgl_vehiculos_auto) {
                tglCamioneta.setChecked(false);
            } else if (buttonView.getId() == R.id.tgl_vehiculos_camioneta) {
                tglAuto.setChecked(false);
            }
        }
    }

    public void guardarRegistroEnWebService(){
        DialogoCargando.mostrarDialogo(Vehiculos.this);
        boolean esEditar = idVehiculo != -1?true:false;
        WebServ ws = new WebServ();
        ws.registrarVehiculo(usuario.getIdUsuario(),vehiculoDTO, vehiculos,new Runnable() {
            @Override
            public void run() {
                if(validarVehiculoWS() && validarListaVehiculosWS()){
                    guardarDatosLocales();
                    DialogoCargando.ocultarDialogo();
                    startActivity(new Intent(Vehiculos.this, ListaVehiculos.class));
                }
            }
        }, new Runnable() {
            @Override
            public void run() {
                DialogoCargando.ocultarDialogo();
                mostrarMensajeErrorWS();
            }
        }, esEditar);
    }

    private boolean validarDatos() {
        if(marca == null) {
            new MsgToast(this, "Debe elegir una marca", false, TipoImportanciaToast.ERROR.getId());
            return false;
        }
        if(modelo == null) {
            new MsgToast(this, "Debe elegir un modelo", false, TipoImportanciaToast.ERROR.getId());
            return false;
        }
        if(etPlacas.getText().toString().isEmpty()) {
            new MsgToast(this, "Debe escribir las placas", false, TipoImportanciaToast.ERROR.getId());
            return false;
        }
        if(etCodigo.getText().toString().isEmpty()) {
            new MsgToast(this, "Debe escribir la llave del vehículo", false, TipoImportanciaToast.ERROR.getId());
            return false;
        }
        if(etCodigoInv.getText().toString().isEmpty()) {
            new MsgToast(this, "Debe escribir la llave de invitación", false, TipoImportanciaToast.ERROR.getId());
            return false;
        }
        if(etTelefono.getText().toString().isEmpty()) {
            new MsgToast(this, "Debe escribir el teléfono", false, TipoImportanciaToast.ERROR.getId());
            return false;
        }
        if(!ValidarTelefono.validar10digitos(etTelefono.getText().toString())){
            new MsgToast(this, "El teléfono debe tener 10 dígitos", false, TipoImportanciaToast.ERROR.getId());
            return false;
        }
        if (!tglAuto.isChecked() && !tglCamioneta.isChecked()) {
            new MsgToast(this, "Debe elegir un tipo de vehículo", false, TipoImportanciaToast.ERROR.getId());
            return false;
        }
        convertirAvehiculoDTO();
        return true;
    }

    public void mostrarConfirmacionEdicion(){
        try {
            DialogoConfirmacion.mostrarDialogo(this,"Confirmación","¿Desea guardar los cambios que realizó" +
                    " en el vehículo?", "Confirmar", "Salir", new Runnable() {
                @Override
                public void run() {
                    guardarRegistroEnWebService();
                }
            }, null);
        }catch (Exception ex){
            Log.d("ERROR EN DIALOG: ",ex.getMessage());
            new MsgToast(this,"Error: "+ex.getMessage(), true, TipoImportanciaToast.ERROR.getId());
        }
    }

    public void mostrarConfirmacion() {
        try {
            DialogoConfirmacion.mostrarDialogo(this, "Confirmación", "¿Desea registrar este vehículo?", "Confirmar", "Salir",
                    new Runnable() {
                        @Override
                        public void run() {
                            guardarRegistroEnWebService();
                        }
                    }, null);
        }catch (Exception ex){
            Log.d("ERROR EN DIALOG: ",ex.getMessage());
            new MsgToast(this,"Error: "+ex.getMessage(), true, TipoImportanciaToast.ERROR.getId());
        }
    }

    public void mostrarDatosParaEditar(Integer idVehiculo){
        BaseDatos base = new BaseDatos(this);
        base.abrir();
        VehiculoData vData = new VehiculoData(base);
        vehiculo = vData.obtenerVehiculoPorId(idVehiculo);
        CatalogoModelos cModelo = new CatalogoModelos(base);
        Modelo modelo = cModelo.obtenerModeloPorId(vehiculo.getIdModelo());

        base.cerrar();
        int pos = 0;
        for(Marca marcaAux : marcas){
            if(marcaAux.getIdMarca() == modelo.getIdMarca()){
                break;
            }
            pos++;
        }
        spinnerMarca.setSelection(pos, false);
        obtenerModelosPorMarca(modelo.getIdMarca());

        pos = 0;
        for(Modelo modeloAux : modelos){
            if(modeloAux.getIdModelo().intValue() == modelo.getIdModelo().intValue()){
                break;
            }
            pos++;
        }
        final int posModelo = pos;
        spinnerModelo.post(new Runnable() {
            @Override
            public void run() {
                spinnerModelo.setSelection(posModelo, false);
            }
        });

        etPlacas.setText(vehiculo.getPlacas());
        etTelefono.setText(vehiculo.getTelefono());
        obtenerLlavesParaEdicion(idVehiculo);
        if(!vehiculo.isTitular()) {
            etTelefono.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            etCodigo.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            etCodigoInv.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }
        tglAuto.setChecked(vehiculo.getTipo() == TipoVehiculoEnum.AUTO.getId() ? true : false);
        tglCamioneta.setChecked(vehiculo.getTipo() == TipoVehiculoEnum.CAMIONETA.getId() ? true : false);
    }

    private List<Llave> armarLlaves(Integer idVehiculo){
        List<Llave> llaves = new ArrayList<>();
        if(idVehiculo!=null){
            Llave llaveV = new Llave();
            llaveV.setIdVehiculo(idVehiculo);
            llaveV.setIdTipoLlave(TipoLlaveEnum.LLAVE_VEHICULO.getId());
            llaveV.setLlave(etCodigo.getText().toString());
            llaveV.setTitular(true);
            Llave llaveIn = new Llave();
            llaveIn.setIdVehiculo(idVehiculo);
            llaveIn.setIdTipoLlave(TipoLlaveEnum.LLAVE_INVITACION.getId());
            llaveIn.setLlave(etCodigoInv.getText().toString());
            llaveIn.setTitular(true);
            llaves.add(llaveV);
            llaves.add(llaveIn);
        }
        return llaves;
    }

    private void obtenerLlavesParaEdicion(Integer idVehiculo){
        BaseDatos base  = new BaseDatos(this);
        base.abrir();
        LlavesData llaveData = new LlavesData(base);
        List<Llave> llaves = llaveData.obtenerLlavesPorIdVehiculo(idVehiculo);
        base.cerrar();
        for(Llave llave : llaves){
            if(llave.getIdTipoLlave() == TipoLlaveEnum.LLAVE_VEHICULO.getId()){
                etCodigo.setText(llave.getLlave());
            }else if(llave.getIdTipoLlave() == TipoLlaveEnum.LLAVE_INVITACION.getId()){
                etCodigoInv.setText(llave.getLlave());
            }
        }
    }

    public void obtenerUsuario(){
        BaseDatos base  =  new BaseDatos(this);
        base.abrir();
        UsuarioData usData = new UsuarioData(base);
        usuario = usData.obtenerDatosPersonales();
        base.cerrar();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("Vehiculos", "Entró a onDestroy()");
    }

    public void guardarDatosLocales(){
        BaseDatos base = new BaseDatos(this);
        base.abrir();
        VehiculoData vehiculoData = new VehiculoData(base);
        vehiculoData.borrarPorTipoUsuario(TipoUsuarioEnum.TITULAR.getId());
        for(VehiculoDTO vehiculoDTO: vehiculos) {
            Vehiculo vehiculo = new Vehiculo();
            vehiculo.setIdVehiculo(vehiculoDTO.getId());
            vehiculo.setTitular(true);
            vehiculo.setIdUsuario(usuario.getIdUsuario());
            vehiculo.setTelefono(vehiculoDTO.getTelefono());
            vehiculo.setIdModelo(vehiculoDTO.getModelo().getIdModelo());
            vehiculo.setPlacas(vehiculoDTO.getPlacas());
            vehiculo.setStatus(vehiculoDTO.getStatus().getId());
            vehiculo.setTipo(vehiculoDTO.getTipo().getId());
            vehiculoData.insertarRegistro(vehiculo);
        }

        DestinatarioData destinatarioData = new DestinatarioData(base);
        destinatarioData.eliminarTodo();
        for(VehiculoDTO vehiculoDTO : vehiculos){
            for(Destinatario destinatario : vehiculoDTO.getDestinatarios()){
                destinatarioData.insertarDestinatario(destinatario);
            }
        }

        LlavesData llavesData = new LlavesData(base);
        llavesData.eliminarPorTipoUsuario(TipoUsuarioEnum.TITULAR.getId());
        for(VehiculoDTO vehiculoDTO : vehiculos){
            for(Llave llave : vehiculoDTO.getLlaves()){
                llavesData.insertarLlave(llave);
            }
        }
        base.cerrar();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(idVehiculo==-1) {
                    new MsgToast(getApplicationContext(), "La vehículo se guardó exitosamente", false, TipoImportanciaToast.INFO.getId());
                }else{
                    new MsgToast(getApplicationContext(), "La vehículo se actualizó exitosamente", false, TipoImportanciaToast.INFO.getId());
                }
            }
        });
    }

    public void mostrarMensajeErrorWS(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new MsgToast(Vehiculos.this, "Ocurrió un error de comunicación con el Web Service", false,
                        TipoImportanciaToast.ERROR.getId());
            }
        });
    }

    public boolean validarVehiculoWS(){
        if(vehiculoDTO.getId() == -1){
            mensajeErrorAlInsertar();
            return false;
        }
        if(vehiculoDTO.getId() == -2){
            mensajeErrorVehiculoDuplicado();
            return false;
        }

        return true;
    }

    public boolean validarListaVehiculosWS(){
        for (VehiculoDTO veh : vehiculos) {
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

    public void convertirAvehiculoDTO(){
        BaseDatos base = new BaseDatos(this);
        base.abrir();
        TipoStatusData statusData = new TipoStatusData(base);
        TipoVehiculoData tipoData = new TipoVehiculoData(base);
        CatalogoModelos cModelos = new CatalogoModelos(base);
        CatalogoMarcas cMarca = new CatalogoMarcas(base);

        vehiculoDTO.setPlacas(etPlacas.getText().toString());
        vehiculoDTO.setTelefono(ValidarTelefono.obtener10digitos(etTelefono.getText().toString()));
        vehiculoDTO.setTipo(tipoData.obtenerTipoVehiculoPorId(tglAuto.isChecked()?
                TipoVehiculoEnum.AUTO.getId():TipoVehiculoEnum.CAMIONETA.getId()));
        vehiculoDTO.setModelo(cModelos.obtenerModeloPorId(modelo.getIdModelo()));
        vehiculoDTO.setMarca(cMarca.obtenerMarcaPorId(vehiculoDTO.getModelo().getIdMarca()));

        if(idVehiculo == -1){
            vehiculoDTO.setId(0);
            vehiculoDTO.setStatus(statusData.obtenerTipoStatusPorId(TipoStatusEnum.ACTIVADO.getId()));
            vehiculoDTO.setLlaves(armarLlaves(0));
        }else{
            vehiculoDTO.setId(vehiculo.getIdVehiculo());
            vehiculoDTO.setStatus(statusData.obtenerTipoStatusPorId(vehiculo.getStatus()));
            vehiculoDTO.setLlaves(armarLlaves(vehiculo.getIdVehiculo()));
        }

        base.cerrar();
    }

    public void mensajeError(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new MsgToast(Vehiculos.this, "Se recibió información incompleta, intente de nuevo por favor", false,
                        TipoImportanciaToast.ERROR.getId());
            }
        });
    }

    public void mensajeErrorAlInsertar(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new MsgToast(Vehiculos.this, "No se pudo guardar el vehículo en el Web Service", false,
                        TipoImportanciaToast.ERROR.getId());
            }
        });
    }

    public void mensajeErrorVehiculoDuplicado(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new MsgToast(Vehiculos.this, "El vehiculo ya está registrado, verifique el teléfono y las placas", false,
                        TipoImportanciaToast.ERROR.getId());
            }
        });
    }

    public void mostrarConfirmacionBorrar(){
        try {
            DialogoConfirmacion.mostrarDialogo(this, "Confirmación", "¿Desea eliminar este vehículo?","Eliminar",
                    "Cancelar", new Runnable() {
                        @Override
                        public void run() {
                            if(vehiculo.isTitular()) {
                                eliminarVehiculoEnWS();
                            }else{
                                eliminarVehiculoLocal();
                                mensajeExitoEliminar();
                                startActivity(new Intent(Vehiculos.this, ListaVehiculos.class));
                            }
                        }
                    }, null);
        } catch (Exception e) {
            new MsgToast(this, "Error al abrir el diálogo", false, TipoImportanciaToast.ERROR.getId());
        }
    }

    public void eliminarVehiculoEnWS(){
        DialogoCargando.mostrarDialogo(Vehiculos.this);
        WebServ ws = new WebServ();
        ws.eliminarVehiculo(usuario.getIdUsuario(),vehiculo.getIdVehiculo(),vehiculos,new Runnable() {
            @Override
            public void run() {
                eliminarVehiculoLocal();
                DialogoCargando.ocultarDialogo();
                mensajeExitoEliminar();
                startActivity(new Intent(Vehiculos.this, ListaVehiculos.class));
            }
        }, new Runnable() {
            @Override
            public void run() {
                DialogoCargando.ocultarDialogo();
                mensajeErrorAlEliminar();
            }
        }, new Runnable() {
            @Override
            public void run() {
                DialogoCargando.ocultarDialogo();
                mostrarMensajeErrorWS();
            }
        });
    }

    public void eliminarVehiculoLocal(){
        BaseDatos base = new BaseDatos(this);
        base.abrir();
        DestinatarioData destData = new DestinatarioData(base);
        destData.eliminarDestinatarioPorIdVehiculo(vehiculo.getIdVehiculo());
        LlavesData llaveData = new LlavesData(base);
        llaveData.eliminarLlavesPorIdVehiculo(vehiculo.getIdVehiculo());
        VehiculoData vehData = new VehiculoData(base);
        vehData.borrarVehiculoPorId(vehiculo.getIdVehiculo());
        base.cerrar();
    }

    public void mensajeErrorAlEliminar(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new MsgToast(getApplicationContext(),"Ocurrió un error al intentar eliminar el vehículo", false,
                        TipoImportanciaToast.ERROR.getId());
            }
        });
    }

    public void mensajeExitoEliminar(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new MsgToast(getApplicationContext(),"El vehículo se eliminó exitosamente", false,
                        TipoImportanciaToast.INFO.getId());
            }
        });
    }
}
