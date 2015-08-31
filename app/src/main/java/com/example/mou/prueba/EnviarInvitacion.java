package com.example.mou.prueba;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.mou.Enum.TipoImportanciaToast;
import com.example.mou.Utilerias.MsgToast;


public class EnviarInvitacion extends ActionBarActivity implements View.OnClickListener{

   LinearLayout layoutDest2, layoutDest3, layoutDest4;
   TextView tvMarcamModelo, tvPlacas;
   EditText etDest1, etDest2, etDest3, etDest4;
   Button btnEnviar;
   ImageButton btnBorrar2, btnBorrar3, btnBorrar4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enviar_invitacion);
        init();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_enviar_invitacion, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.menuInvitacion_btnAgregar) {
//            boolean band = false;
            if(layoutDest2.getVisibility() != View.VISIBLE) {
                layoutDest2.setVisibility(View.VISIBLE);
                return true;
            }
            if(layoutDest2.getVisibility() == View.VISIBLE && layoutDest3.getVisibility() != View.VISIBLE){
                layoutDest3.setVisibility(View.VISIBLE);
                return true;
            }
            if(layoutDest2.getVisibility() == View.VISIBLE && layoutDest3.getVisibility() == View.VISIBLE &&
                                                        layoutDest4.getVisibility() != View.VISIBLE){
                layoutDest4.setVisibility(View.VISIBLE);
                return true;
            }

            new MsgToast(this, "Se aceptan máximo 4 destinatarios", false, TipoImportanciaToast.ERROR.getId());

        }

        return super.onOptionsItemSelected(item);
    }

    public void init(){
        tvMarcamModelo = (TextView)findViewById(R.id.tv_invitacion_marcaymodelo);
        tvPlacas = (TextView)findViewById(R.id.tv_invitacion_placas);
        etDest1 = (EditText)findViewById(R.id.et_invitacion_dest1);
        etDest2 = (EditText)findViewById(R.id.et_invitacion_dest2);
        etDest3 = (EditText)findViewById(R.id.et_invitacion_dest3);
        etDest4 = (EditText)findViewById(R.id.et_invitacion_dest4);
        btnEnviar = (Button)findViewById(R.id.btn_invitacion_enviar);
        btnBorrar2 = (ImageButton)findViewById(R.id.btn_invitacion_borrar2);
        btnBorrar3 = (ImageButton)findViewById(R.id.btn_invitacion_borrar3);
        btnBorrar4 = (ImageButton)findViewById(R.id.btn_invitacion_borrar4);

        etDest1.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        etDest2.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        etDest3.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        etDest4.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        btnEnviar.setOnClickListener(this);
        layoutDest2 = (LinearLayout)findViewById(R.id.layout_invitacion_dest2);
        layoutDest3 = (LinearLayout)findViewById(R.id.layout_invitacion_dest3);
        layoutDest4 = (LinearLayout)findViewById(R.id.layout_invitacion_dest4);
        btnBorrar2.setOnClickListener(this);
        btnBorrar3.setOnClickListener(this);
        btnBorrar4.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_invitacion_borrar2:
                etDest2.setText("");
                layoutDest2.setVisibility(View.GONE);
                break;
            case R.id.btn_invitacion_borrar3:
                etDest3.setText("");
                layoutDest3.setVisibility(View.GONE);
                break;
            case R.id.btn_invitacion_borrar4:
                etDest4.setText("");
                layoutDest4.setVisibility(View.GONE);
                break;
        }
    }
}
