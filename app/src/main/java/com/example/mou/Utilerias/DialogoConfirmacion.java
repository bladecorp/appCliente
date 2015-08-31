package com.example.mou.Utilerias;

import android.app.Activity;
import android.app.Dialog;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.mou.Enum.TipoImportanciaToast;
import com.example.mou.model.Telefono;
import com.example.mou.prueba.InvitacionActivity;
import com.example.mou.prueba.MainActivity;
import com.example.mou.prueba.R;

/**
 * Created by Mou on 07/07/2015.
 */
public class DialogoConfirmacion {


    public static final void mostrarDialogo(final Activity activity, final String titulo, final String mensaje, final String tituloBotonOK,
                          final String tituloBotonCancel, final Runnable clickAceptar, final Runnable clickCancel) throws Exception{

        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialogo_confirmacion);
        TextView tvTitulo = (TextView)dialog.findViewById(R.id.tv_dialogo_vehiculos_titulo);
        tvTitulo.setText(titulo);
        TextView tvMensaje = (TextView)dialog.findViewById(R.id.tv_dialogo_vehiculos_mensaje);
        tvMensaje.setText(mensaje);
        EditText etPassword = (EditText)dialog.findViewById(R.id.et_dialogo_vehiculos_pswd);
        etPassword.setVisibility(View.INVISIBLE);
        Button btnSalir = (Button)dialog.findViewById(R.id.btn_dialogo_vehiculos_salir);
        Button btnRegistrar = (Button)dialog.findViewById(R.id.btn_dialogo_vehiculos_registrar);
        btnSalir.setText(tituloBotonCancel);
        btnRegistrar.setText(tituloBotonOK);
        btnSalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickCancel != null) {
                    clickCancel.run();
                    dialog.dismiss();
                } else {
                    dialog.dismiss();
                }
            }
        });
        btnRegistrar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                  if(clickAceptar != null){
                      clickAceptar.run();
                      dialog.dismiss();
                  }else {
                      dialog.dismiss();
                  }
                }
            });

            dialog.show();
        }//fin método


    public static final void mostrarDialogoContrasenia(final Activity activity, final String titulo, final String mensaje,
                                    final String password, final Runnable clickAceptar, final Runnable clickCancel) throws Exception{

        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialogo_confirmacion);
        TextView tvTitulo = (TextView)dialog.findViewById(R.id.tv_dialogo_vehiculos_titulo);
        tvTitulo.setText(titulo);
        TextView tvMensaje = (TextView)dialog.findViewById(R.id.tv_dialogo_vehiculos_mensaje);
        tvMensaje.setText(mensaje);
        final EditText etPassword = (EditText)dialog.findViewById(R.id.et_dialogo_vehiculos_pswd);
    //    etPassword.setVisibility(View.INVISIBLE);
        Button btnSalir = (Button)dialog.findViewById(R.id.btn_dialogo_vehiculos_salir);
        Button btnRegistrar = (Button)dialog.findViewById(R.id.btn_dialogo_vehiculos_registrar);
        btnSalir.setText("Cancelar");
        btnRegistrar.setText("Aceptar");
        btnSalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickCancel != null) {
                    clickCancel.run();
                    dialog.dismiss();
                } else {
                    if(password == null){
                        dialog.dismiss();
                        if(activity instanceof MainActivity) {
                            System.exit(0);
                        }
                    }
                    dialog.dismiss();
                }
            }
        });
        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(clickAceptar != null){
                    if(password != null) {
                        if (password.contentEquals(etPassword.getText().toString())) {
                            clickAceptar.run();
                            dialog.dismiss();
                        } else {
                            new MsgToast(activity.getBaseContext(), "La constraseña es incorrecta", false, TipoImportanciaToast.ERROR.getId());
                        }
                    }else{
                        if(!etPassword.getText().toString().isEmpty()) {
                            InformacionLocal.guardarPasswordLocal(activity.getBaseContext(), etPassword.getText().toString());
                            new MsgToast(activity.getBaseContext(), "La constraseña se guardó correctamente", false, TipoImportanciaToast.INFO.getId());
                            dialog.dismiss();
                        }else{
                            new MsgToast(activity.getBaseContext(), "Debe escribir una contraseña", false, TipoImportanciaToast.ERROR.getId());
                        }
                    }

                }else {
                    if(!etPassword.getText().toString().isEmpty()) {
                        InformacionLocal.guardarPasswordLocal(activity.getBaseContext(), etPassword.getText().toString());
                        new MsgToast(activity.getBaseContext(), "La constraseña se guardó correctamente", false, TipoImportanciaToast.INFO.getId());
                        dialog.dismiss();
                    }else{
                        new MsgToast(activity.getBaseContext(), "Debe escribir una contraseña", false, TipoImportanciaToast.ERROR.getId());
                    }
                }
            }
        });

        dialog.show();
    }//fin método

    public static final void mostrarDialogoTelefono(final Activity activity, final String titulo, final String mensaje,
      final Runnable guardar, final Runnable cancelar, final ArrayAdapter<Telefono> adaptador, final Integer idUsuario,
         final Telefono telefono) throws Exception{

        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialogo_confirmacion);
        TextView tvTitulo = (TextView)dialog.findViewById(R.id.tv_dialogo_vehiculos_titulo);
        tvTitulo.setText(titulo);
        TextView tvMensaje = (TextView)dialog.findViewById(R.id.tv_dialogo_vehiculos_mensaje);
        tvMensaje.setText(mensaje);
        EditText etPassword = (EditText)dialog.findViewById(R.id.et_dialogo_vehiculos_pswd);
        etPassword.setVisibility(View.GONE);
        final EditText etTel = (EditText)dialog.findViewById(R.id.et_dialogo_vehiculos_tel);
        etTel.setVisibility(View.VISIBLE);
        etTel.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        if(telefono!=null) {
            etTel.setText(telefono.getTelefono());
        }
        Button btnSalir = (Button)dialog.findViewById(R.id.btn_dialogo_vehiculos_salir);
        Button btnRegistrar = (Button)dialog.findViewById(R.id.btn_dialogo_vehiculos_registrar);
        btnSalir.setText("Cancelar");
        btnRegistrar.setText("Guardar");
        btnSalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cancelar != null) {
                    cancelar.run();
                    dialog.dismiss();
                } else {
                    dialog.dismiss();
                }
            }
        });
        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(guardar != null){
                    String tel = ValidarTelefono.obtener10digitos(etTel.getText().toString());
                    if(tel.length() == 10) {
                        Telefono t = new Telefono();
                        if(telefono!=null) {
                            adaptador.remove(telefono);
                            t.setId(telefono.getId());
                        }else{
                            t.setId(0);
                        }
                        t.setIdUsuario(idUsuario);
                        t.setTelefono(tel);
                        adaptador.add(t);
                        guardar.run();
                        dialog.dismiss();
                    }else{
                        new MsgToast(activity.getBaseContext(),"El teléfono debe tener 10 dígitos", false, TipoImportanciaToast.ERROR.getId());
                    }
                }else {
                    dialog.dismiss();
                }
            }
        });

        dialog.show();
    }//fin método

    public static final void infoStatusInvitacion(Activity activity, final Runnable clickCerrar)throws Exception{
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dlg_info_status_inv);

        Button btnSalir = (Button)dialog.findViewById(R.id.btn_infostatusinv);
        btnSalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickCerrar != null) {
                    clickCerrar.run();
                    dialog.dismiss();
                } else {
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
    }

    public static final void dialogoEnviarMensaje(final Activity activity, final String titulo, final String mensaje,
                                     final Runnable clickSMS, final Runnable clickWEB){
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.confirmacion_envio_mensaje);
        TextView tvTitulo = (TextView)dialog.findViewById(R.id.tv_dialogo_enviarMensaje_titulo);
        tvTitulo.setText(titulo);
        TextView tvMensaje = (TextView)dialog.findViewById(R.id.tv_dialogo_envioMensaje);
        tvMensaje.setText(mensaje);
        Button btnSMS = (Button)dialog.findViewById(R.id.btn_dialogo_mensaje_SMS);
        Button btnWEB = (Button)dialog.findViewById(R.id.btn_dialogo_mensaje_WEB);
        if(activity instanceof InvitacionActivity){
            btnSMS.setCompoundDrawables(null, null, null, null);
            btnWEB.setCompoundDrawables(null, null, null, null);
        }
        Button btnSalir = (Button)dialog.findViewById(R.id.btn_dialogo_mensajeSalir);
        if(activity instanceof InvitacionActivity){
            btnSMS.setText("Rechazar");
            btnWEB.setText("Aceptar");
        }

        btnSMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickSMS != null) {
                    clickSMS.run();
                    dialog.dismiss();
                } else {
                    dialog.dismiss();
                }
            }
        });
        btnWEB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(clickWEB != null){
                    clickWEB.run();
                    dialog.dismiss();
                }else {
                    dialog.dismiss();
                }
            }
        });
        btnSalir.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public static final void dialogoInformativo(final Activity activity, final String titulo, final String mensaje){
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialogo_informativo);
        TextView tvTitulo = (TextView)dialog.findViewById(R.id.tv_dialogo_info_titulo);
        tvTitulo.setText(titulo);
        TextView tvMensaje = (TextView)dialog.findViewById(R.id.tv_dialogo_info_mensaje);
        tvMensaje.setText(mensaje);
        Button btnSalir = (Button)dialog.findViewById(R.id.btn_dialogo_info_cerrar);
        btnSalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    dialog.dismiss();
            }
        });

        dialog.show();
    }

    }// fin clase

