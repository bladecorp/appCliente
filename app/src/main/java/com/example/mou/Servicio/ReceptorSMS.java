package com.example.mou.Servicio;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import com.example.mou.Enum.TipoImportanciaToast;
import com.example.mou.Utilerias.MsgToast;

import java.util.logging.Logger;

public class ReceptorSMS extends BroadcastReceiver {

    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
    public static final String TELEFONO_TWILIO = "5549998734";
    public static final String TELEFONO = "telefono";
    public static final String SMS_RECIBIDO = "sms";

    public ReceptorSMS() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(SMS_RECEIVED)) {

            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                Object[] pdus = (Object[]) bundle.get("pdus");
                if (pdus.length == 0) {
                    return;
                }
                SmsMessage[] messages = new SmsMessage[pdus.length];
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < pdus.length; i++) {
                    messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                    sb.append(messages[i].getMessageBody());
                }

                String telefono = messages[0].getOriginatingAddress();
                new MsgToast(context, "Se interceptó el mensaje: "+telefono, false, TipoImportanciaToast.INFO.getId());
                if(telefono.contentEquals(TELEFONO_TWILIO)) {
                    String mensaje = sb.toString();
                    Intent intent1 = new Intent(context, ServicioSMS.class);
                    intent1.putExtra(TELEFONO, TELEFONO_TWILIO);
                    intent1.putExtra(SMS_RECIBIDO, mensaje);
                    context.startService(intent1);
                    abortBroadcast();
                }
                new MsgToast(context, "No se interceptó el mensaje"+telefono, false, TipoImportanciaToast.INFO.getId());
            }
        }
    }
}
