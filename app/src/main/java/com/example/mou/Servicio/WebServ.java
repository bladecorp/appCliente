package com.example.mou.Servicio;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.mou.Enum.TipoImportanciaToast;
import com.example.mou.Enum.TipoUsuarioEnum;
import com.example.mou.Utilerias.MsgToast;
import com.example.mou.dto.UsuarioDTO;
import com.example.mou.dto.VehiculoDTO;
import com.example.mou.model.Destinatario;
import com.example.mou.model.HistoricoMensajes;
import com.example.mou.model.Llave;
import com.example.mou.model.Marca;
import com.example.mou.model.Modelo;
import com.example.mou.model.Telefono;
import com.example.mou.model.TipoStatus;
import com.example.mou.model.TipoVehiculo;
import com.example.mou.model.Usuario;
import com.example.mou.model.Vehiculo;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by Mou on 04/08/2015.
 */
public class WebServ {
    private static final Integer TIME_OUT = 7000;
    private static final String NAMESPACE = "http://interfaces.tesisws.proyecto.com/";
    private static final String URL = "http://proyectotesis.ddns.net:8080/webservice/services/procesarPeticion?wsdl";

    private static final String METODO_DESTINATARIOS_POR_IDVEHICULO = "obtenerDestinatariosPorIdVehiculo";
    private static final String METODO_INSERTAR_DESTINATARIO = "insertarDestinatario";
    private static final String METODO_ACTUALIZAR_DESTINATARIO = "actualizarDestinatario";
    private static final String METODO_ELIMINAR_DESTINATARIO = "eliminarDestinatario";
    private static final String METODO_ELIMINAR_TODOS_DESTINATARIOS = "eliminarDestinatariosPorIdVehiculo";
    private static final String METODO_ACTUALIZAR_INVITACION = "aceptarOrechazarInvitacion";
    private static final String METODO_ENVIAR_INVITACION = "enviarInvitacion";

    private static final String METODO_TELEFONOS_POR_IDUSUARIO = "obtenerTelefonosPorIdUsuario";
    private static final String METODO_GUARDAR_TELEFONOS_POR_IDUSUARIO = "guardarTelefonosPorIdUsuario";

    private static final String METODO_HISTORICO_MENSAJES = "obtenerHistoricoPorFecha";

    private static final String METODO_REGISTRAR_USUARIO = "registrarUsuario";
    private static final String METODO_USUARIO_POR_ID = "obtenerUsuarioPorId";
    private static final String METODO_OBTENER_USUARIO = "obtenerUsuario";
    private static final String METODO_ACTUALIZAR_USUARIO = "actualizarUsuario";

    private static final String METODO_VEHICULOS_POR_IDUSUARIO = "obtenerVehiculosPorIdUsuario";
    private static final String METODO_INSERTAR_VEHICULO = "insertarVehiculo";
    private static final String METODO_ACTUALIZAR_VEHICULO = "actualizarVehiculo";
    private static final String METODO_ELIMINAR_VEHICULO_PORID = "eliminarVehiculoPorId";
    private static final String METODO_ELIMINAR_VEHICULOS_POR_IDUSUARIO = "eliminarVehiculosPorIdUsuario";
    private static final String METODO_LLAVES_POR_IDVEHICULO = "obtenerLlaverPorIdVehiculo";
    private static final String METODO_ACTUALIZAR_STATUS_POR_IDVEHICULO = "actualizarStatusVehiculo";
    private static final String METODO_ENVIAR_REVISION_SENSORES = "enviarMensajeRevisionSensores";


    private void aceptarOrechazarInvitacion(Integer idVehiculo, Integer idDestinatario, Integer statusInv,
                                Vehiculo vehiculo, HttpTransportSE transporte)throws Exception{
        SoapObject request = new SoapObject(NAMESPACE,METODO_ACTUALIZAR_INVITACION);
        request.addProperty("idVehiculo", idVehiculo);
        request.addProperty("idDestinatario", idDestinatario);
        request.addProperty("statusInvitacion", statusInv);

        SoapSerializationEnvelope envase = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envase.dotNet=false;
        envase.setOutputSoapObject(request);

        transporte.call("", envase);
        SoapObject resul = (SoapObject) envase.getResponse();
        //List<SoapObject> resul = (List<SoapObject>) envase.getResponse();
        if(resul!=null) {
            if (resul.hasProperty("id") && resul.hasProperty("placas") && resul.hasProperty("telefono")
                    && resul.hasProperty("idUsuario") && resul.hasProperty("idTipoVehiculo") && resul.hasProperty("idTipoStatus")
                     && resul.hasProperty("idModelo")) {

                vehiculo.setIdModelo(Integer.parseInt(resul.getPropertyAsString("idModelo")));
                vehiculo.setIdUsuario(Integer.parseInt(resul.getPropertyAsString("idUsuario")));
                vehiculo.setIdVehiculo(Integer.parseInt(resul.getPropertyAsString("id")));
                vehiculo.setStatus(Integer.parseInt(resul.getPropertyAsString("idTipoStatus")));
                vehiculo.setPlacas(resul.getPropertyAsString("placas"));
                vehiculo.setTelefono(resul.getPropertyAsString("telefono"));
                vehiculo.setTipo(Integer.parseInt(resul.getPropertyAsString("idTipoVehiculo")));
                vehiculo.setTitular(false);
            }else{vehiculo = null;}
        }else{vehiculo = null;}

    }

    private void obtenerHistoricoPorFecha(Integer idVehiculo,Integer mes, Integer anio, List<HistoricoMensajes> mensajes,
                                                                        HttpTransportSE transporte)throws Exception{
        SoapObject request = new SoapObject(NAMESPACE,METODO_HISTORICO_MENSAJES);
        request.addProperty("idVehiculo", idVehiculo);
        request.addProperty("mes", mes);
        request.addProperty("anio", anio);

        SoapSerializationEnvelope envase = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envase.dotNet=false;
        envase.setOutputSoapObject(request);

        transporte.call("", envase);
        SoapObject so = (SoapObject)envase.bodyIn;
        int tamanio = so.getPropertyCount();
        if(tamanio > 0){
            List<SoapObject> listaResponse = new ArrayList<>();
            if(tamanio == 1){
                SoapObject response = (SoapObject) envase.getResponse();
                listaResponse.add(response);
            }else{
                listaResponse = (List<SoapObject>) envase.getResponse();
            }
            for (SoapObject res : listaResponse) {
                if (res.hasProperty("id") && res.hasProperty("fecha") && res.hasProperty("idSubMensaje") &&
                        res.hasProperty("idTipoMensaje") && res.hasProperty("idVehiculo") && res.hasProperty("mensaje")) {
                    HistoricoMensajes hist = new HistoricoMensajes();
                    hist.setId(Integer.parseInt(res.getPropertyAsString("id")));
                    String[] fechaCruda = res.getPropertyAsString("fecha").split("T");
                    String[] fechaDia = fechaCruda[0].split("-");
                    String[] fechaHora = fechaCruda[1].split("-");
                    String fecha = fechaDia[2]+"-"+fechaDia[1]+"-"+fechaDia[0]+", "+fechaHora[0];
                    hist.setFecha(fecha);
                    hist.setIdSubMensaje(Integer.parseInt(res.getPropertyAsString("idSubMensaje")));
                    hist.setIdTipoMensaje(Integer.parseInt(res.getPropertyAsString("idTipoMensaje")));
                    hist.setIdVehiculo(Integer.parseInt(res.getPropertyAsString("idVehiculo")));
                    hist.setMensaje(res.getPropertyAsString("mensaje"));
                    hist.setTipoUsuario(TipoUsuarioEnum.TITULAR.getId());
                    mensajes.add(hist);
                }
            }
        }
    }

    private boolean enviarRevisionSensores(Integer idVehiculo, HttpTransportSE transporte)throws Exception{
        SoapObject request = new SoapObject(NAMESPACE,METODO_ENVIAR_REVISION_SENSORES);
        request.addProperty("idVehiculo", idVehiculo);

        SoapSerializationEnvelope envase = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envase.dotNet=false;
        envase.setOutputSoapObject(request);

        transporte.call("", envase);
        SoapPrimitive response = (SoapPrimitive) envase.getResponse();
        if(response!=null){
            return Boolean.parseBoolean(response.toString());
        }
        return false;
    }

    private boolean enviarInvitacion(Integer idVehiculo, Integer idDestinatario, HttpTransportSE transporte)throws Exception{
        SoapObject request = new SoapObject(NAMESPACE,METODO_ENVIAR_INVITACION);
        request.addProperty("idVehiculo", idVehiculo);
        request.addProperty("idDestinatario", idDestinatario);

        SoapSerializationEnvelope envase = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envase.dotNet=false;
        envase.setOutputSoapObject(request);

        transporte.call("", envase);
        SoapPrimitive response = (SoapPrimitive) envase.getResponse();
        if(response!=null){
            return Boolean.parseBoolean(response.toString());
        }
        return false;
    }

    private boolean eliminarTodosDestinatarios(Integer idVehiculo, HttpTransportSE transporte)throws Exception{
        SoapObject request = new SoapObject(NAMESPACE, METODO_ELIMINAR_TODOS_DESTINATARIOS);
        request.addProperty("idVehiculo", idVehiculo);

        SoapSerializationEnvelope envase = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envase.dotNet=false;
        envase.setOutputSoapObject(request);

        transporte.call("", envase);
        SoapPrimitive response = (SoapPrimitive) envase.getResponse();
        if(response!=null){
            return Boolean.parseBoolean(response.toString());
        }
        return false;
    }

    private boolean eliminarDestinatario(Integer idDestinatario, HttpTransportSE transporte)throws Exception{

        SoapObject request = new SoapObject(NAMESPACE,METODO_ELIMINAR_DESTINATARIO);
        request.addProperty("idDestinatario", idDestinatario);

        SoapSerializationEnvelope envase = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envase.dotNet=false;
        envase.setOutputSoapObject(request);

        transporte.call("", envase);
        SoapPrimitive response = (SoapPrimitive) envase.getResponse();
        if(response!=null){
            return Boolean.parseBoolean(response.toString());
        }
        return false;
    }

    private boolean registrarDestinatarioEnVehiculo(Destinatario dest, HttpTransportSE transporte,
                                                    boolean esEditar)throws Exception{
        SoapObject request;
        SoapObject destinatario = new SoapObject("","destinatario");
        if(esEditar) {
            request = new SoapObject(NAMESPACE,METODO_ACTUALIZAR_DESTINATARIO);
            destinatario.addProperty("id", dest.getId());
        }else{
            request = new SoapObject(NAMESPACE,METODO_INSERTAR_DESTINATARIO);
            destinatario.addProperty("id", 0);
        }
        destinatario.addProperty("nombre",dest.getNombre());
        destinatario.addProperty("aPaterno", dest.getaPaterno());
        destinatario.addProperty("aMaterno", dest.getaMaterno());
        destinatario.addProperty("telefono", dest.getTelefono());
        destinatario.addProperty("statusInvitacion", dest.getStatusInv());
        destinatario.addProperty("idVehiculo", dest.getIdVehiculo());

        request.addSoapObject(destinatario);

        SoapSerializationEnvelope envase = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envase.dotNet=false;
        envase.setOutputSoapObject(request);

        transporte.call("", envase);
        SoapPrimitive response = (SoapPrimitive) envase.getResponse();
        if(response!=null){
            if(esEditar) {
                return Boolean.parseBoolean(response.toString());
            }else{
                int idDest = Integer.parseInt(response.toString());
                dest.setId(idDest);
                return idDest > 0?true:false;
            }
        }
        return false;
    }

    private boolean actualizarStatusVehiculo(Integer idVehiculo, Integer tipoStatus,
                                                HttpTransportSE transporte)throws Exception{
           SoapObject request = new SoapObject(NAMESPACE,METODO_ACTUALIZAR_STATUS_POR_IDVEHICULO);
           request.addProperty("idVehiculo", idVehiculo);
           request.addProperty("tipoStatus", tipoStatus);

           SoapSerializationEnvelope envase = new SoapSerializationEnvelope(SoapEnvelope.VER11);
           envase.dotNet=false;
           envase.setOutputSoapObject(request);

           transporte.call("", envase);
           SoapPrimitive response = (SoapPrimitive) envase.getResponse();
           if(response!=null){
               return Boolean.parseBoolean(response.toString());
           }
           return false;
       }

    private boolean eliminarVehiculosPorIdUsuario(Integer idUsuario,HttpTransportSE transporte)throws  Exception{
        SoapObject request = new SoapObject(NAMESPACE, METODO_ELIMINAR_VEHICULOS_POR_IDUSUARIO);
        request.addProperty("idUsuario", idUsuario);

        SoapSerializationEnvelope envase = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envase.dotNet=false;
        envase.setOutputSoapObject(request);

        transporte.call("", envase);
        SoapPrimitive response = (SoapPrimitive) envase.getResponse();
        if(response!=null){
            return Boolean.parseBoolean(response.toString());
        }
        return false;
    }

    private boolean eliminarVehiculoPorId(Integer idVehiculo, HttpTransportSE transporte)throws Exception{
        SoapObject request = new SoapObject(NAMESPACE, METODO_ELIMINAR_VEHICULO_PORID);
        request.addProperty("idVehiculo", idVehiculo);

        SoapSerializationEnvelope envase = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envase.dotNet=false;
        envase.setOutputSoapObject(request);

        transporte.call("", envase);
        SoapPrimitive response = (SoapPrimitive) envase.getResponse();
        if(response!=null){
            return Boolean.parseBoolean(response.toString());
        }
        return false;
    }

    private void insertarVehiculo(Integer idUsuario, VehiculoDTO vehiculo, HttpTransportSE transporte,
                                            boolean esEditar)throws Exception{

        SoapObject request;
        SoapObject soVehiculo = new SoapObject("","vehiculoDTO");
        if(esEditar){
            soVehiculo.addProperty("id",vehiculo.getId());
            request = new SoapObject(NAMESPACE, METODO_ACTUALIZAR_VEHICULO);
        }else{
            soVehiculo.addProperty("id", 0);
            request = new SoapObject(NAMESPACE, METODO_INSERTAR_VEHICULO);
        }

        SoapObject soMarca = new SoapObject("","marca");
        soMarca.addProperty("id",vehiculo.getMarca().getIdMarca());
        soMarca.addProperty("nombre",vehiculo.getMarca().getNombre());
        soVehiculo.addSoapObject(soMarca);

        SoapObject soModelo = new SoapObject("","modelo");
        soModelo.addProperty("id",vehiculo.getModelo().getIdModelo());
        soModelo.addProperty("nombre",vehiculo.getModelo().getNombre());
        soModelo.addProperty("idMarca", vehiculo.getMarca().getIdMarca());
        soVehiculo.addSoapObject(soModelo);

        soVehiculo.addProperty("placas",vehiculo.getPlacas());
        soVehiculo.addProperty("telefono",vehiculo.getTelefono());

        SoapObject soTipo = new SoapObject("", "tipo");
        soTipo.addProperty("id",vehiculo.getTipo().getId());
        soTipo.addProperty("tipo",vehiculo.getTipo().getTipo());
        soVehiculo.addSoapObject(soTipo);

        SoapObject soStatus = new SoapObject("", "status");
        soStatus.addProperty("id",vehiculo.getStatus().getId());
        soStatus.addProperty("tipo",vehiculo.getStatus().getTipo());
        soVehiculo.addSoapObject(soStatus);

        for(Llave llave : vehiculo.getLlaves()){
            SoapObject soLlave = new SoapObject("","llaves");
            soLlave.addProperty("idTipoLlave", llave.getIdTipoLlave());
            soLlave.addProperty("idVehiculo",llave.getIdVehiculo());
            soLlave.addProperty("codigo",llave.getLlave());
            soVehiculo.addSoapObject(soLlave);
        }

        request.addSoapObject(soVehiculo);
        request.addProperty("idUsuario", idUsuario);

        SoapSerializationEnvelope envase = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envase.dotNet=false;
        envase.setOutputSoapObject(request);

        transporte.call("", envase);
        SoapPrimitive response = (SoapPrimitive) envase.getResponse();
        if(response!=null) {
            if(esEditar){
                boolean exito = Boolean.parseBoolean(response.toString());
                if(!exito){
                    vehiculo.setId(-2);
                }
            }else {
                int idVehiculo = Integer.parseInt(response.toString());
                vehiculo.setId(idVehiculo);
            }
        }else{
            vehiculo.setId(-1);
        }
    }

    private void registrarUsuario(Usuario usuario, String telefono, HttpTransportSE transporte)throws Exception{
        SoapObject request = new SoapObject(NAMESPACE, METODO_REGISTRAR_USUARIO);
        SoapObject user = new SoapObject("","usuario");
        user.addProperty("id",usuario.getIdUsuario());
        user.addProperty("nombre", usuario.getNombre());
        user.addProperty("aPaterno", usuario.getaPaterno());
        user.addProperty("aMaterno", usuario.getaMaterno());
        user.addProperty("usuario", usuario.getUsuario());
        user.addProperty("password", usuario.getContrasena());
        request.addSoapObject(user);
        request.addProperty("telefono", telefono);
        SoapSerializationEnvelope envase = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envase.dotNet=false;
        envase.setOutputSoapObject(request);

        transporte.call("", envase);
        SoapPrimitive resul = (SoapPrimitive) envase.getResponse();
        if(resul!=null) {
            int idUsuario = Integer.parseInt(resul.toString());
            usuario.setIdUsuario(idUsuario);
        }else{
            usuario.setIdUsuario(null);
        }
    }

    private void obtenerUsuarioPorId(Usuario usuario, HttpTransportSE transporte)throws Exception{

        SoapObject request = new SoapObject(NAMESPACE, METODO_USUARIO_POR_ID);
        request.addProperty("idUsuario",usuario.getIdUsuario());
        SoapSerializationEnvelope envase = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envase.dotNet=false;
        envase.setOutputSoapObject(request);

        transporte.call("", envase);
        SoapObject resul = (SoapObject) envase.getResponse();
        //List<SoapObject> resul = (List<SoapObject>) envase.getResponse();
        if(resul!=null) {
            if (resul.hasProperty("id") && resul.hasProperty("nombre") && resul.hasProperty("aPaterno")
                    && resul.hasProperty("usuario") && resul.hasProperty("password")) {
                usuario.setIdUsuario(Integer.parseInt(resul.getPropertyAsString("id")));
                usuario.setNombre(resul.getPropertyAsString("nombre"));
                usuario.setaPaterno(resul.getPropertyAsString("aPaterno"));
                usuario.setUsuario(resul.getPropertyAsString("usuario"));
                usuario.setContrasena(resul.getPropertyAsString("password"));
                if(resul.hasProperty("aMaterno")){
                    usuario.setaMaterno(!resul.getPropertyAsString("aMaterno").contentEquals("anyType{}")?
                            resul.getPropertyAsString("aMaterno"): "N/D");
                }
            }
        }
    }

    private void obtenerUsuarioPorNombre(Usuario usuario, HttpTransportSE transporte)throws  Exception{
        SoapObject request = new SoapObject(NAMESPACE, METODO_OBTENER_USUARIO);
        request.addProperty("usuario",usuario.getUsuario());
        SoapSerializationEnvelope envase = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envase.dotNet=false;
        envase.setOutputSoapObject(request);
        transporte.call("", envase);
        SoapObject resul = (SoapObject) envase.getResponse();
        if(resul!=null) {
            if (resul.hasProperty("id") && resul.hasProperty("nombre") && resul.hasProperty("aPaterno")
                    && resul.hasProperty("usuario") && resul.hasProperty("password")) {
                usuario.setIdUsuario(Integer.parseInt(resul.getPropertyAsString("id")));
                usuario.setNombre(resul.getPropertyAsString("nombre"));
                usuario.setaPaterno(resul.getPropertyAsString("aPaterno"));
                usuario.setUsuario(resul.getPropertyAsString("usuario"));
                usuario.setContrasena(resul.getPropertyAsString("password"));
                if(resul.hasProperty("aMaterno")){
                    usuario.setaMaterno(!resul.getPropertyAsString("aMaterno").contentEquals("anyType{}")?
                            resul.getPropertyAsString("aMaterno"): "");
                }
            }
        }
    }

    private void actualizarUsuario(Usuario usuario,List<Telefono> telefonos, HttpTransportSE transporte) throws Exception{
        SoapObject request = new SoapObject(NAMESPACE, METODO_ACTUALIZAR_USUARIO);
        SoapObject user = new SoapObject("","usuario");
        user.addProperty("id",usuario.getIdUsuario());
        user.addProperty("nombre", usuario.getNombre());
        user.addProperty("aPaterno", usuario.getaPaterno());
        user.addProperty("aMaterno", usuario.getaMaterno());
        user.addProperty("usuario", usuario.getUsuario());
        user.addProperty("password", usuario.getContrasena());
        request.addSoapObject(user);

        for(Telefono telefono : telefonos){
            SoapObject tels = new SoapObject("","listaTelefonos");
            tels.addProperty("id",telefono.getId());
            tels.addProperty("idUsuario",telefono.getIdUsuario());
            tels.addProperty("telefono", telefono.getTelefono());
            request.addSoapObject(tels);
        }

        SoapSerializationEnvelope envase = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envase.dotNet=false;
     //   envase.addMapping(NAMESPACE,"usuario",new Usuario().getClass());
     //   envase.addMapping(NAMESPACE,"listaTelefonos",new Telefono().getClass());
     //   envase.implicitTypes=true; envase.setAddAdornments(false);
        envase.setOutputSoapObject(request);

        transporte.call("", envase);
        SoapPrimitive resul = (SoapPrimitive) envase.getResponse();
        String r = "true"; String res = String.valueOf(resul.getValue());
        if(!r.contentEquals(res)){
            usuario.setIdUsuario(null);
        }

    }

    private void obtenerTelefonosPorIdUsuario(Integer idUsuario, List<Telefono> telefonos,
                                                        HttpTransportSE transporte)throws Exception{

        SoapObject request = new SoapObject(NAMESPACE, METODO_TELEFONOS_POR_IDUSUARIO);
        request.addProperty("idUsuario",idUsuario);
        SoapSerializationEnvelope envase = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envase.dotNet=false;
        envase.setOutputSoapObject(request);

        transporte.call("", envase);

        SoapObject so = (SoapObject)envase.bodyIn;
        int tamanio = so.getPropertyCount();
        if(tamanio > 0){
            List<SoapObject> listaResponse = new ArrayList<>();
            if(tamanio == 1){
                SoapObject response = (SoapObject) envase.getResponse();
                listaResponse.add(response);
            }else{
                listaResponse = (List<SoapObject>) envase.getResponse();
            }
            for (SoapObject res : listaResponse) {
                if (res.hasProperty("id") && res.hasProperty("telefono") && res.hasProperty("idUsuario")) {
                    Telefono telefono = new Telefono();
                    telefono.setId(Integer.parseInt(res.getPropertyAsString("id")));
                    telefono.setTelefono(res.getPropertyAsString("telefono"));
                    telefono.setIdUsuario(Integer.parseInt(res.getPropertyAsString("idUsuario")));
                    telefonos.add(telefono);
                }
            }
        }

    }

    private void obtenerVehiculosPorIdUsuario(Integer idUsuario, List<VehiculoDTO> vehiculos,
                                                        HttpTransportSE transporte)throws Exception{

        SoapObject request = new SoapObject(NAMESPACE, METODO_VEHICULOS_POR_IDUSUARIO);
        request.addProperty("idUsuario", idUsuario);
        SoapSerializationEnvelope envase = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envase.dotNet = false;
        envase.setOutputSoapObject(request);

        transporte.call("", envase);
        SoapObject so = (SoapObject)envase.bodyIn;
        int tamanio = so.getPropertyCount();
        if(tamanio > 0){
            List<SoapObject> listaResponse = new ArrayList<>();
            if(tamanio == 1){
                SoapObject response = (SoapObject) envase.getResponse();
                listaResponse.add(response);
            }else{
                listaResponse = (List<SoapObject>) envase.getResponse();
            }
            for (SoapObject res : listaResponse) {
                if (res.hasProperty("id") && res.hasProperty("marca") && res.hasProperty("modelo") && res.hasProperty("placas")
                        && res.hasProperty("telefono") && res.hasProperty("tipo") && res.hasProperty("status") && res.hasProperty("llaves")) {
                    VehiculoDTO vehiculoDTO = new VehiculoDTO();
                    vehiculoDTO.setId(Integer.parseInt(res.getPropertyAsString("id")));
                    vehiculoDTO.setPlacas(res.getPropertyAsString("placas"));
                    vehiculoDTO.setTelefono(res.getPropertyAsString("telefono"));
                    SoapObject soMarca = (SoapObject) res.getProperty("marca");

                    SoapObject soModelo = (SoapObject) res.getProperty("modelo");
                    SoapObject soTipo = (SoapObject) res.getProperty("tipo");
                    SoapObject soStatus = (SoapObject) res.getProperty("status");
                    List<SoapObject> soLlaves = new ArrayList<>();
                    soLlaves.add((SoapObject) res.getProperty(7));
                    soLlaves.add((SoapObject) res.getProperty(8));

                    vehiculoDTO.getMarca().setIdMarca(Integer.parseInt(soMarca.getPropertyAsString("id")));
                    vehiculoDTO.getMarca().setNombre(soMarca.getPropertyAsString("nombre"));
                    vehiculoDTO.getModelo().setIdMarca(Integer.parseInt(soModelo.getPropertyAsString("idMarca")));
                    vehiculoDTO.getModelo().setIdModelo(Integer.parseInt(soModelo.getPropertyAsString("id")));
                    vehiculoDTO.getModelo().setNombre(soModelo.getPropertyAsString("nombre"));
                    vehiculoDTO.getTipo().setId(Integer.parseInt(soTipo.getPropertyAsString("id")));
                    vehiculoDTO.getTipo().setTipo(soTipo.getPropertyAsString("tipo"));
                    vehiculoDTO.getStatus().setId(Integer.parseInt(soStatus.getPropertyAsString("id")));
                    vehiculoDTO.getStatus().setTipo(soStatus.getPropertyAsString("tipo"));


                    for (SoapObject soapObj : soLlaves) {
                        if (soapObj.hasProperty("idTipoLlave") && soapObj.hasProperty("idVehiculo") && soapObj.hasProperty("codigo")) {
                            Llave llave = new Llave();
                            llave.setIdTipoLlave(Integer.parseInt(soapObj.getPropertyAsString("idTipoLlave")));
                            llave.setIdVehiculo(Integer.parseInt(soapObj.getPropertyAsString("idVehiculo")));
                            llave.setLlave(soapObj.getPropertyAsString("codigo"));
                            llave.setTitular(true);
                            vehiculoDTO.getLlaves().add(llave);
                        }
                    }
                    vehiculos.add(vehiculoDTO);
                }
            }
        }
    }

    private void obtenerDestinatariosPorIdVehiculo(Integer idVehiculo, List<Destinatario> destinatarios,
                                                  HttpTransportSE transporte)throws Exception{
        SoapObject request = new SoapObject(NAMESPACE, METODO_DESTINATARIOS_POR_IDVEHICULO);
        request.addProperty("idVehiculo",idVehiculo);
        SoapSerializationEnvelope envase = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envase.dotNet=false;
        envase.setOutputSoapObject(request);

        transporte.call("",envase);

        SoapObject so = (SoapObject) envase.bodyIn;
        int tamanio = so.getPropertyCount();
        if(tamanio > 0){
            List<SoapObject> listaResponse = new ArrayList<>();
            if(tamanio == 1){
                SoapObject response = (SoapObject) envase.getResponse();
                listaResponse.add(response);
            }else{
                listaResponse = (List<SoapObject>) envase.getResponse();
            }
            for (SoapObject soapObj : listaResponse) {
                if (soapObj.hasProperty("id") && soapObj.hasProperty("nombre") && soapObj.hasProperty("aPaterno") &&
                        soapObj.hasProperty("telefono") && soapObj.hasProperty("idVehiculo")) {
                    Destinatario dest = new Destinatario();
                    dest.setId(Integer.parseInt(soapObj.getPropertyAsString("id")));
                    dest.setNombre(soapObj.getPropertyAsString("nombre"));
                    dest.setaPaterno(soapObj.getPropertyAsString("aPaterno"));
                    dest.setTelefono(soapObj.getPropertyAsString("telefono"));
                    dest.setStatusInv(Integer.parseInt(soapObj.getPropertyAsString("statusInvitacion")));
                    dest.setIdVehiculo(Integer.parseInt(soapObj.getPropertyAsString("idVehiculo")));
                    if(soapObj.hasProperty("aMaterno")){
                        dest.setaMaterno(!soapObj.getPropertyAsString("aMaterno").contentEquals("anyType{}")?
                                soapObj.getPropertyAsString("aMaterno"): "");
                    }
                    destinatarios.add(dest);
                }
            }
        }

    }

    private void obtenerLlavesPorIdVehiculo(Integer idVehiculo, List<Llave> llaves, HttpTransportSE transporte) throws  Exception{

        SoapObject request = new SoapObject(NAMESPACE, METODO_LLAVES_POR_IDVEHICULO);
        request.addProperty("idVehiculo", idVehiculo);
        SoapSerializationEnvelope envase = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envase.dotNet=false;
        envase.setOutputSoapObject(request);
        transporte.call("", envase);

        List<SoapObject> resul = (List<SoapObject>) envase.getResponse();
        if(resul!=null) {
            for (SoapObject soapObj : resul) {
                if (soapObj.hasProperty("idTipoLlave") && soapObj.hasProperty("idVehiculo") && soapObj.hasProperty("codigo")) {
                    Llave llave = new Llave();
                    llave.setIdTipoLlave(Integer.parseInt(soapObj.getPropertyAsString("idTipoLlave")));
                    llave.setIdVehiculo(Integer.parseInt(soapObj.getPropertyAsString("idVehiculo")));
                    llave.setLlave(soapObj.getPropertyAsString("codigo"));
                    llaves.add(llave);
                }
            }
        }
    }

    private void obtenerHistoricoPorIdVehiculo(Integer idVehiculo){

    }

    //**********************************************************************//
    //                      FIN MÉTODOS WEB SERVICE                         //
    //**********************************************************************//


    public void obtenerInformacionUsuario(final UsuarioDTO usuarioDTO, final String password,
                         final Runnable postTransaccion, final Runnable errorEnTransaccion){
        Thread thread = new Thread(){
            @Override
            public void run() {
                HttpTransportSE transporte = new HttpTransportSE(URL, TIME_OUT);

                try {
                    obtenerUsuarioPorNombre(usuarioDTO.getUsuario(), transporte);
                    if(usuarioDTO.getUsuario().getContrasena()!=null && usuarioDTO.getUsuario().getContrasena().contentEquals(password)){
                        obtenerTelefonosPorIdUsuario(usuarioDTO.getUsuario().getIdUsuario(),usuarioDTO.getTelefonos(),transporte);
                    }
                    postTransaccion.run();
                } catch (Exception e) {
                    errorEnTransaccion.run();
                }
            }
        };
        thread.start();
    }

    public void actualizarInformacion(final UsuarioDTO usuarioDTO, final List<VehiculoDTO> vehiculosDTO,
                             final Runnable postTransaccion, final Runnable errorEnTransaccion){
        Thread thread = new Thread(){
            @Override
            public void run() {
                HttpTransportSE transporte = new HttpTransportSE(URL,TIME_OUT);
                try {
                    obtenerUsuarioPorId(usuarioDTO.getUsuario(), transporte);
                    obtenerTelefonosPorIdUsuario(usuarioDTO.getUsuario().getIdUsuario(), usuarioDTO.getTelefonos(), transporte);
                    obtenerVehiculosPorIdUsuario(usuarioDTO.getUsuario().getIdUsuario(),vehiculosDTO,transporte);
                    for(VehiculoDTO vehiculoDTO : vehiculosDTO){
                        obtenerDestinatariosPorIdVehiculo(vehiculoDTO.getId(),vehiculoDTO.getDestinatarios(),transporte);
                    }
                    postTransaccion.run();
                }catch (Exception e){
                    errorEnTransaccion.run();
                }
            }
        };
        thread.start();
    }

    public void actualizarUsuarioYtelefonos(final Usuario usuario, final List<Telefono> telefonos,
                           final Runnable postTransaccion, final Runnable errorEnTransaccion) {
        Thread thread = new Thread(){
            @Override
            public void run() {
                HttpTransportSE transporte = new HttpTransportSE(URL,TIME_OUT);
                transporte.debug=true;
                try {
                    actualizarUsuario(usuario, telefonos, transporte);
                    telefonos.clear();
                    if(usuario.getIdUsuario()!=null){
                        obtenerUsuarioPorId(usuario, transporte);
                        obtenerTelefonosPorIdUsuario(usuario.getIdUsuario(),telefonos,transporte);
                    }
                    postTransaccion.run();
                }catch (Exception e){
                    errorEnTransaccion.run();
                }
            }

        };

        thread.start();
    }

    public void insertarUsuario(final Usuario usuario, final String telefono, final Runnable postTransaccion,
                                                        final Runnable errorEnTransaccion){
        Thread thread = new Thread(){
            @Override
            public void run() {
                HttpTransportSE transporte = new HttpTransportSE(URL,TIME_OUT);
                transporte.debug=true;
                try {
                    registrarUsuario(usuario, telefono, transporte);
                    postTransaccion.run();
                }catch (Exception e){
                    errorEnTransaccion.run();
                }
            }

        };

        thread.start();
    }

    public void registrarVehiculo(final Integer idUsuario,final VehiculoDTO vehiculodDTO,final List<VehiculoDTO> vehiculos,
                  final Runnable postTransaccion,final Runnable errorEnTransaccion, final boolean esEditar){
        Thread thread = new Thread(){
            @Override
            public void run() {
                HttpTransportSE transporte = new HttpTransportSE(URL,TIME_OUT);
                transporte.debug=true;
                try {
                    insertarVehiculo(idUsuario, vehiculodDTO, transporte, esEditar);
                    if(vehiculodDTO.getId() != -1 || vehiculodDTO.getId() != -2){
                        obtenerVehiculosPorIdUsuario(idUsuario,vehiculos,transporte);
                        for(VehiculoDTO veh : vehiculos) {
                            obtenerDestinatariosPorIdVehiculo(veh.getId(),veh.getDestinatarios(),transporte);
                        }
                    }
                    postTransaccion.run();
                }catch (Exception e){
                    errorEnTransaccion.run();
                }
            }

        };

        thread.start();
    }

    public void eliminarVehiculo(final Integer idUsuario, final Integer idVehiculo, final List<VehiculoDTO> vehiculos,
          final Runnable postTransaccion, final Runnable errorAlEliminar, final Runnable errorEnTransaccion){
        Thread thread = new Thread(){
            @Override
            public void run() {
                HttpTransportSE transporte = new HttpTransportSE(URL,TIME_OUT);
                transporte.debug=true;
                try {
                    boolean exito = eliminarVehiculoPorId(idVehiculo, transporte);
                    if(exito){
                        obtenerVehiculosPorIdUsuario(idUsuario,vehiculos,transporte);
                        for(VehiculoDTO veh : vehiculos) {
                            obtenerDestinatariosPorIdVehiculo(veh.getId(),veh.getDestinatarios(),transporte);
                        }
                        postTransaccion.run();
                    }else{
                    errorAlEliminar.run(); }
                }catch (Exception e){
                    errorEnTransaccion.run();
                }
            }

        };

        thread.start();
    }

    public void eliminarVehiculosPorIdUsuarioWS(final Integer idUsuario,final List<Vehiculo> vehiculos,
             final Runnable postTransaccion,final Runnable errorAlEliminar, final Runnable errorEnTransaccion){
        Thread thread = new Thread(){
            @Override
            public void run() {
                HttpTransportSE transporte = new HttpTransportSE(URL,TIME_OUT);
                transporte.debug=true;
                try {
                    boolean exito = eliminarVehiculosPorIdUsuario(idUsuario, transporte);
                    if(exito){
                        vehiculos.clear();
                        postTransaccion.run();
                    }else{
                        errorAlEliminar.run(); }
                }catch (Exception e){
                    errorEnTransaccion.run();
                }
            }

        };
        thread.start();
    }

    public void cambiarStatusEnWS(final Integer idVehiculo, final Integer tipoStatus, final Runnable postTransaccion,
                          final Runnable errorAlCambiarStatus, final Runnable errorEnTransaccion){
        Thread thread = new Thread(){
            @Override
            public void run() {
                HttpTransportSE transporte = new HttpTransportSE(URL,TIME_OUT);
                transporte.debug=true;
                try {
                    boolean exito = actualizarStatusVehiculo(idVehiculo, tipoStatus, transporte);
                    if(exito){
                        postTransaccion.run();
                    }else{
                        errorAlCambiarStatus.run(); }
                }catch (Exception e){
                    errorEnTransaccion.run();
                }
            }

        };
        thread.start();
    }

    public void registrarDestinatarioEnWS(final Destinatario dest, final List<Destinatario> destinatarios,
            final boolean esEditar, final Runnable postTransaccion, final Runnable errorAlGuardar,
            final Runnable errorEnTransaccion){
        Thread thread = new Thread(){
            @Override
            public void run() {
                HttpTransportSE transporte = new HttpTransportSE(URL,TIME_OUT);
                transporte.debug=true;
                try {
                    boolean exito = registrarDestinatarioEnVehiculo(dest, transporte, esEditar);
                    if(exito){
                        obtenerDestinatariosPorIdVehiculo(dest.getIdVehiculo(),destinatarios,transporte);
                        postTransaccion.run();
                    }else{
                        errorAlGuardar.run();
                    }
                }catch (Exception e){
                    errorEnTransaccion.run();
                }
            }

        };
        thread.start();
    }

    public void eliminarDestinatarioEnWS(final Integer idDestinatario,final Integer idVehiculo,final List<Destinatario> destinatarios,
         final Runnable postTransaccion, final Runnable errorAlEliminar, final Runnable errorEnTransaccion){
        Thread thread = new Thread(){
            @Override
            public void run() {
                HttpTransportSE transporte = new HttpTransportSE(URL,TIME_OUT);
                transporte.debug=true;
                try {
                    boolean exito = eliminarDestinatario(idDestinatario, transporte);
                    if(exito){
                        obtenerDestinatariosPorIdVehiculo(idVehiculo,destinatarios,transporte);
                        postTransaccion.run();
                    }else{
                        errorAlEliminar.run();
                    }
                }catch (Exception e){
                    errorEnTransaccion.run();
                }
            }

        };
        thread.start();
    }

    public void eliminarTodosDestinatariosEnWS(final Integer idVehiculo, final Runnable postTransaccion,
             final Runnable errorAlEliminar, final Runnable errorEnTransaccion){
        Thread thread = new Thread(){
            @Override
            public void run() {
                HttpTransportSE transporte = new HttpTransportSE(URL,TIME_OUT);
                transporte.debug=true;
                try {
                    boolean exito = eliminarTodosDestinatarios(idVehiculo, transporte);
                    if(exito){
                        postTransaccion.run();
                    }else{
                        errorAlEliminar.run();
                    }
                }catch (Exception e){
                    errorEnTransaccion.run();
                }
            }

        };
        thread.start();
    }

    public void enviarInvitacionPorWS(final Integer idVehiculo, final Integer idDestinatario, final Runnable postTransaccion,
                              final Runnable errorAlEnviar, final Runnable errorEnTransaccion){
        Thread thread = new Thread(){
            @Override
            public void run() {
                HttpTransportSE transporte = new HttpTransportSE(URL,TIME_OUT);
                transporte.debug=true;
                try {
                    boolean exito = enviarInvitacion(idVehiculo, idDestinatario, transporte);
                    if(exito){
                        postTransaccion.run();
                    }else{
                        errorAlEnviar.run();
                    }
                }catch (Exception e){
                    errorEnTransaccion.run();
                }
            }

        };
        thread.start();
    }

    public void enviarRevisionSensoresPorWS(final Integer idVehiculo, final Runnable postTransaccion,
                           final Runnable errorAlEnviar, final Runnable errorEnTransaccion){
        Thread thread = new Thread(){
            @Override
            public void run() {
                HttpTransportSE transporte = new HttpTransportSE(URL,TIME_OUT);
                transporte.debug=true;
                try {
                    boolean exito = enviarRevisionSensores(idVehiculo, transporte);
                   // boolean exito = true;
                    if(exito){
                        postTransaccion.run();
                    }else{
                        errorAlEnviar.run();
                    }
                }catch (Exception e){
                    errorEnTransaccion.run();
                }
            }

        };
        thread.start();
    }

    public void obtenerHistoricoMensajesPorWS(final Integer idVehiculo, final Integer mes, final Integer anio,
         final List<HistoricoMensajes> mensajes, final Runnable postTransaccion,
                                                                final Runnable errorEnTransaccion){
        Thread thread = new Thread(){
            @Override
            public void run() {
                HttpTransportSE transporte = new HttpTransportSE(URL,TIME_OUT);
                transporte.debug=true;
                try {
                    obtenerHistoricoPorFecha(idVehiculo, mes, anio, mensajes, transporte);
                    postTransaccion.run();
                }catch (Exception e){
                    errorEnTransaccion.run();
                }
            }

        };
        thread.start();
    }

    public void aceptarOrechazarInvitacionPorWS(final Integer idVehiculo, final Integer idDestinatario, final Integer statusInv,
                                    final Vehiculo vehiculo, final List<Llave> llaves, final Runnable postTransaccion,
                                    final Runnable mensajeInvitacionRechazada, final Runnable errorEnTransaccion){
        Thread thread = new Thread(){
            @Override
            public void run() {
                HttpTransportSE transporte = new HttpTransportSE(URL,TIME_OUT);
                transporte.debug=true;
                try {
                    aceptarOrechazarInvitacion(idVehiculo, idDestinatario, statusInv, vehiculo, transporte);
                    if(vehiculo.getIdVehiculo() != null){
                        obtenerLlavesPorIdVehiculo(idVehiculo,llaves,transporte);
                        postTransaccion.run();
                    }else{
                        mensajeInvitacionRechazada.run();
                    }
                }catch (Exception e){
                    errorEnTransaccion.run();
                }
            }

        };
        thread.start();
    }

}// FIN CLASE
