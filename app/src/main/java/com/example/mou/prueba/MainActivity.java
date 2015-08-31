package com.example.mou.prueba;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.example.mou.Enum.TipoImportanciaToast;
import com.example.mou.Utilerias.InformacionLocal;
import com.example.mou.Utilerias.DialogoConfirmacion;
import com.example.mou.Utilerias.MsgToast;


public class MainActivity extends ActionBarActivity implements View.OnClickListener, View.OnLongClickListener{

    EditText et_constrasena;
    String contr = "", contrasenia="";
    Button btn_uno, btn_dos, btn_tres, btn_cuatro, btn_cinco, btn_seis, btn_siete, btn_ocho,
            btn_nueve, btn_cero, btn_borrar, btn_ok;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        contrasenia = obtenerContrasenaBD();
        if(contrasenia == ""){
            try {
                DialogoConfirmacion.mostrarDialogoContrasenia(this,"Crear Contraseña Local","Máximo 5 números",null,null, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else{
            new MsgToast(this, "La contraseña es: "+contrasenia, false, TipoImportanciaToast.INFO.getId());
        }

    }
    @Override
    protected void onRestart() {
        super.onRestart();
        contrasenia = obtenerContrasenaBD();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.registrar) {
            startActivity(new Intent(this, RegistroActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.main_bt_uno:

                contr = et_constrasena.getText().toString();
                mensaje(contr);
                llenarContrasena(contr, '1');
                break;
            case R.id.main_bt_dos:
                contr = et_constrasena.getText().toString();
                mensaje(contr);
                llenarContrasena(contr, '2');
                break;
            case R.id.main_bt_tres:
                contr = et_constrasena.getText().toString();
                mensaje(contr);
                llenarContrasena(contr, '3');
                break;
            case R.id.main_bt_cuatro:
                contr = et_constrasena.getText().toString();
                mensaje(contr);
                llenarContrasena(contr, '4');
                break;
            case R.id.main_bt_cinco:
                contr = et_constrasena.getText().toString();
                mensaje(contr);
                llenarContrasena(contr, '5');
                break;
            case R.id.main_bt_seis:
                contr = et_constrasena.getText().toString();
                mensaje(contr);
                llenarContrasena(contr, '6');
                break;
            case R.id.main_bt_siete:
                contr = et_constrasena.getText().toString();
                mensaje(contr);
                llenarContrasena(contr, '7');
                break;
            case R.id.main_bt_ocho:
                contr = et_constrasena.getText().toString();
                mensaje(contr);
                llenarContrasena(contr, '8');
                break;
            case R.id.main_bt_nueve:
                contr = et_constrasena.getText().toString();
                mensaje(contr);
                llenarContrasena(contr, '9');
                break;
            case R.id.main_bt_cero:
                contr = et_constrasena.getText().toString();
                mensaje(contr);
                llenarContrasena(contr, '0');
                break;
            case R.id.main_bt_borrar:
                contr = et_constrasena.getText().toString();
                borrarCaracter(contr);
                break;
            case R.id.main_bt_ok:
                contr = et_constrasena.getText().toString();
                validarContrasena();
                break;
        }
    }

    private void mensaje(String contr){
        if(contr.length() == 5){
          new MsgToast(this, "la contraseña debe tener máximo 5 dígitos", false
                  , TipoImportanciaToast.INFO.getId());
        }
    }
    private void borrarCaracter(String contr){
        if(contr.length() > 0){
            et_constrasena.setText(contr.substring(0, contr.length() - 1));
            int posicion = et_constrasena.length();
            Editable editable = et_constrasena.getText();
            Selection.setSelection(editable, posicion);
        }
    }
    private void validarContrasena(){
        contrasenia = obtenerContrasenaBD();

        if (contrasenia != null && contr.contentEquals(contrasenia)) {
            Intent i = new Intent(this, ConfiguracionPrincipal.class);
            startActivity(i);
        } else {
            if(contrasenia == null) {
                new MsgToast(this, "No hay usuarios registrados", false, TipoImportanciaToast.ERROR.getId());
            }else{
                new MsgToast(this, "Password Inválido", false, TipoImportanciaToast.ERROR.getId());
            }
        }
        et_constrasena.setText("");
    }
    private void llenarContrasena(String contr, char num){
        //et_constrasena.setText(contr + num);
        et_constrasena.append(String.valueOf(num));
    }
    private String obtenerContrasenaBD(){
        return InformacionLocal.obtenerPasswordLocal(this);
    }
    private void init(){
        et_constrasena = (EditText)findViewById(R.id.main_et_constrasena);
        btn_uno = (Button)findViewById(R.id.main_bt_uno);
        btn_dos = (Button)findViewById(R.id.main_bt_dos);
        btn_tres = (Button)findViewById(R.id.main_bt_tres);
        btn_cuatro = (Button)findViewById(R.id.main_bt_cuatro);
        btn_cinco = (Button)findViewById(R.id.main_bt_cinco);
        btn_seis = (Button)findViewById(R.id.main_bt_seis);
        btn_siete = (Button)findViewById(R.id.main_bt_siete);
        btn_ocho = (Button)findViewById(R.id.main_bt_ocho);
        btn_nueve = (Button)findViewById(R.id.main_bt_nueve);
        btn_cero = (Button)findViewById(R.id.main_bt_cero);
        btn_borrar = (Button)findViewById(R.id.main_bt_borrar);
        btn_ok = (Button)findViewById(R.id.main_bt_ok);
        btn_uno.setOnClickListener(this);
        btn_dos.setOnClickListener(this);
        btn_tres.setOnClickListener(this);
        btn_cuatro.setOnClickListener(this);
        btn_cinco.setOnClickListener(this);
        btn_seis.setOnClickListener(this);
        btn_siete.setOnClickListener(this);
        btn_ocho.setOnClickListener(this);
        btn_nueve.setOnClickListener(this);
        btn_cero.setOnClickListener(this);
        btn_borrar.setOnClickListener(this);
        btn_borrar.setOnLongClickListener(this);
        btn_ok.setOnClickListener(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        System.exit(0);
    }

    @Override
    public boolean onLongClick(View v) {
        switch(v.getId()){
            case R.id.main_bt_borrar:
                et_constrasena.setText("");
                break;
        }
        return true;
    }
}
