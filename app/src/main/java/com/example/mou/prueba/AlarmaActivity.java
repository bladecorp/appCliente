package com.example.mou.prueba;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.mou.Enum.TipoImportanciaToast;
import com.example.mou.Utilerias.InformacionLocal;
import com.example.mou.Utilerias.MsgToast;


public class AlarmaActivity extends ActionBarActivity implements View.OnClickListener{

    ImageButton btnImagen;
    Button btnLlammar;
    TextView tvMensaje;
    MediaPlayer alarma, voz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarma);
        btnImagen = (ImageButton)findViewById(R.id.btnSilenciarAlarma);
        btnLlammar = (Button)findViewById(R.id.btnLlamarAproveedor);
        tvMensaje = (TextView)findViewById(R.id.tvMensajeAlarma);
        btnImagen.setOnClickListener(this);
        btnLlammar.setOnClickListener(this);
        String sensor = getIntent().getStringExtra("sensor");
        String vehiculo = getIntent().getStringExtra("nombreVehiculo");
        String placas = getIntent().getStringExtra("placas");
        tvMensaje.setText("Se detectó actividad en el sensor de: "+sensor.toUpperCase()
                                            +"\n\nVehiculo: "+vehiculo+"\nPlacas: "+placas);
        Vibrator v = (Vibrator) this.getSystemService(this.VIBRATOR_SERVICE);// This example will cause the phone to vibrate "SOS" in Morse Code
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

// In Morse Code, "s" = "dot-dot-dot", "o" = "dash-dash-dash"
// There are pauses to separate dots/dashes, letters, and words
// The following numbers represent millisecond lengths
        int dot = 200;      // Length of a Morse Code "dot" in milliseconds
        int dash = 500;     // Length of a Morse Code "dash" in milliseconds
        int short_gap = 200;    // Length of Gap Between dots/dashes
        int medium_gap = 500;   // Length of Gap Between Letters
        int long_gap = 1000;    // Length of Gap Between Words
        long[] pattern = {
                0,  // Start immediately
                dot, short_gap, dot, short_gap, dot,    // s
                medium_gap,
                dash, short_gap, dash, short_gap, dash, // o
                medium_gap,
                dot, short_gap, dot, short_gap, dot,    // s
                long_gap
        };

// Only perform this pattern one time (-1 means "do not repeat")
        v.vibrate(pattern, -1);
        alarma = MediaPlayer.create(this, R.raw.emergencias);
        voz = MediaPlayer.create(this, R.raw.male);
        alarma.start();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
               voz.start();
            }
        }, 2500);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_alarma, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
       /* if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnSilenciarAlarma:
                voz.stop();
                alarma.stop();
                break;
            case R.id.btnLlamarAproveedor:
                String telProveedor = InformacionLocal.obtenerTelefonoProveedor(this);
                if(telProveedor.length() == 10) {
                    Intent i = new Intent(Intent.ACTION_CALL);
                    i.setData(Uri.parse("tel:" + telProveedor));
                    startActivity(i);
                }else{
                    new MsgToast(this, "No hay telefono registrado", false, TipoImportanciaToast.ERROR.getId());
                }
            break;
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MainActivity.class));
        super.onBackPressed();
    }
}
