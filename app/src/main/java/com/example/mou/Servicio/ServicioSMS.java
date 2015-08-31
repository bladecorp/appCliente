package com.example.mou.Servicio;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.Vibrator;
import android.util.Log;

import com.example.mou.Enum.MensajeGeneral;
import com.example.mou.Enum.StatusInvitacion;
import com.example.mou.Enum.TipoImportanciaToast;
import com.example.mou.Enum.TipoMensajeEnum;
import com.example.mou.Utilerias.MsgToast;
import com.example.mou.data.BaseDatos;
import com.example.mou.data.CatalogoMarcas;
import com.example.mou.data.CatalogoModelos;
import com.example.mou.data.HistoricoMensajeData;
import com.example.mou.data.InvitacionesData;
import com.example.mou.data.SensoresData;
import com.example.mou.data.TipoMensajeData;
import com.example.mou.data.VehiculoData;
import com.example.mou.model.HistoricoMensajes;
import com.example.mou.model.Invitacion;
import com.example.mou.model.Marca;
import com.example.mou.model.Modelo;
import com.example.mou.model.Sensor;
import com.example.mou.model.Vehiculo;
import com.example.mou.prueba.AlarmaActivity;
import com.example.mou.prueba.MainActivity;
import com.example.mou.prueba.R;

public class ServicioSMS extends Service {

    private String telTwilio;
    private String mensajeRecibido;

    public ServicioSMS() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        new MsgToast(this, "Termina Servicio", false, TipoImportanciaToast.INFO.getId());
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        despertarTelefono();
        telTwilio = intent.getStringExtra(ReceptorSMS.TELEFONO);
        mensajeRecibido = intent.getStringExtra(ReceptorSMS.SMS_RECIBIDO);
        Log.d("Servicio SMS", "TelTwilio: "+telTwilio+", Mensaje: "+mensajeRecibido);
        new MsgToast(this, "Inicia Servicio", false, TipoImportanciaToast.INFO.getId());
        procesarMensaje();
        onDestroy();
        return super.onStartCommand(intent, flags, startId);
    }

    private void procesarMensaje(){
        Log.d("ServicioSMS","Procesando Mensaje...");new MsgToast(this, "Procesando Mensaje...", false, TipoImportanciaToast.INFO.getId());
        String[] mensaje = mensajeRecibido.split(";");
        if(mensaje.length >= 3) {
            int tipoMensaje = Integer.parseInt(mensaje[0]);
            if(tipoMensaje == TipoMensajeEnum.ALARMA.getId()){
                Log.d("ServicioSMS","El mensaje es ALARMA");new MsgToast(this, "El mensaje es ALARMA", false, TipoImportanciaToast.INFO.getId());
                int idSensor = Integer.parseInt(mensaje[1]);
                int idVehiculo = Integer.parseInt(mensaje[2]);
                procesarAlarma(idVehiculo, idSensor);
            }else{
                int tipoSubMensaje = Integer.parseInt(mensaje[1]);
                if(tipoSubMensaje == MensajeGeneral.INVITACION.getId()){
                    if(mensaje.length == 5){
                        Log.d("ServicioSMS","El mensaje es INVITACIÓN");new MsgToast(this, "El mensaje es INVITACIÓN", false, TipoImportanciaToast.INFO.getId());
                        int idModelo = Integer.parseInt(mensaje[2]);
                        int idVehiculo = Integer.parseInt(mensaje[3]);
                        int idDestinatario = Integer.parseInt(mensaje[4]);
                        guardarInvitacion(idVehiculo,idModelo, idDestinatario);
                        notificarUsuario();
                    }

                }
            }

        }
    }

    private void procesarAlarma(Integer idVehiculo, Integer idSensor){
     //   despertarTelefono();
        Log.d("ServicioSMS","Procesando ALARMA");new MsgToast(this, "Procesando ALARMA", false, TipoImportanciaToast.INFO.getId());
        Vehiculo vehiculo = obtenerVehiculo(idVehiculo);
        if(vehiculo != null) {
            Sensor sensor = obtenerSensor(idSensor);
            Intent i = new Intent(this, AlarmaActivity.class);
            i.putExtra("sensor", sensor != null ? sensor.getNombre() : "");
            i.putExtra("nombreVehiculo", obtenerNombreMarcaYmodelo(vehiculo.getIdModelo()));
            i.putExtra("placas", vehiculo.getPlacas());
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        }
    }

    public void guardarInvitacion(Integer idVehiculo, Integer idModelo, Integer idDestinatario ){
        if(idVehiculo != null && idModelo != null && idDestinatario != null){
            Invitacion invitacion = new Invitacion();
            invitacion.setIdVehiculo(idVehiculo);
            invitacion.setIdModelo(idModelo);
            invitacion.setIdDestinatario(idDestinatario);
            invitacion.setIdStatus(StatusInvitacion.RECIBIDA.getId());
            BaseDatos base = new BaseDatos(this);
            base.abrir();
            InvitacionesData invData = new InvitacionesData(base);
            invData.insertarInvitacion(invitacion);
            base.cerrar();
        }

    }

    private void notificarUsuario(){

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);
        Notification n  = new Notification.Builder(this)
                .setContentTitle("Aviso")
                .setContentText("Recibió una invitación")
                .setSmallIcon(R.drawable.auto)
                .setContentIntent(pIntent)
                .setAutoCancel(true).build();
         //       .addAction(R.drawable.icon, "Opcion 1", pIntent)
         //       .addAction(R.drawable.icon, "Opcion 2", pIntent)
         //       .addAction(R.drawable.icon, "Opcion 3", pIntent).build();
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        notificationManager.notify(0, n);
        Vibrator v = (Vibrator) this.getSystemService(this.VIBRATOR_SERVICE);
        v.vibrate(500);
    }

    private Vehiculo obtenerVehiculo(Integer idVehiculo){
        BaseDatos base = new BaseDatos(this);
        base.abrir();
        VehiculoData vehData = new VehiculoData(base);
        Vehiculo vehiculo = vehData.obtenerVehiculoPorId(idVehiculo);
        base.cerrar();
        return vehiculo;
    }

    private Sensor obtenerSensor(Integer idSensor){
        BaseDatos base = new BaseDatos(this);
        base.abrir();
        SensoresData sensorData = new SensoresData(base);
        Sensor sensor = sensorData.obtenerSensorPorId(idSensor);
        base.cerrar();
        return sensor;
    }

    private String obtenerNombreMarcaYmodelo(Integer idModelo){
        String nombre = "";
        BaseDatos base = new BaseDatos(this);
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

   private void despertarTelefono(){
       PowerManager pm = (PowerManager) getSystemService(this.POWER_SERVICE);
       PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK|PowerManager.ACQUIRE_CAUSES_WAKEUP, "bbbb");
       wl.acquire();
   }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
