package com.example.mou.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Mou on 17/01/2015.
 */
public class BaseDatos extends SQLiteOpenHelper {

    public static final String DB_NOMBRE = "basedatos_aplicacion";
    public static final int DB_VERSION = 1;

    Context context;
    private SQLiteDatabase db;

    public BaseDatos(Context context) {
        super(context, DB_NOMBRE, null, DB_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(UsuarioData.CREATE_TABLE);
        db.execSQL(VehiculoData.CREATE_TABLE);
        db.execSQL(CatalogoMarcas.CREATE_TABLE);
        db.execSQL(CatalogoModelos.CREATE_TABLE);
        db.execSQL(CatalogoMarcas.INSERT_MARCAS);
        db.execSQL(CatalogoModelos.ACURA);
        db.execSQL(CatalogoModelos.AUDI);
        db.execSQL(CatalogoModelos.BMW);
        db.execSQL(CatalogoModelos.CADILLAC);
        db.execSQL(CatalogoModelos.CHEVROLET);
        db.execSQL(CatalogoModelos.CHRYSLER);
        db.execSQL(CatalogoModelos.DODGE);
        db.execSQL(CatalogoModelos.FIAT);
        db.execSQL(CatalogoModelos.FORD);
        db.execSQL(CatalogoModelos.GMC);
        db.execSQL(CatalogoModelos.HONDA);
        db.execSQL(CatalogoModelos.JEEP);
        db.execSQL(CatalogoModelos.KIA);
        db.execSQL(CatalogoModelos.MAZDA);
        db.execSQL(CatalogoModelos.MERCEDEZ);
        db.execSQL(CatalogoModelos.NISSAN);
        db.execSQL(CatalogoModelos.PEUGEOT);
        db.execSQL(CatalogoModelos.RENAULT);
        db.execSQL(CatalogoModelos.SEAT);
        db.execSQL(CatalogoModelos.TOYOTA);
        db.execSQL(CatalogoModelos.VOLKSWAGEN);
        db.execSQL(CatalogoModelos.VOLVO);
        db.execSQL(DestinatarioData.CREATE_TABLE);
        db.execSQL(HistoricoMensajeData.CREATE_TABLE);
        db.execSQL(LlavesData.CREATE_TABLE);
        db.execSQL(TelefonoData.CREATE_TABLE);
        db.execSQL(TipoLlaveData.CREATE_TABLE);
        db.execSQL(TipoLlaveData.INSERT_TIPOLLAVE);
        db.execSQL(TipoMensajeData.CREATE_TABLE);
        db.execSQL(TipoMensajeData.INSERT_TIPOMENSAJE);
        db.execSQL(TipoStatusData.CREATE_TABLE);
        db.execSQL(TipoStatusData.INSERT_TIPOSTATUS);
        db.execSQL(TipoVehiculoData.CREATE_TABLE);
        db.execSQL(TipoVehiculoData.INSERT_TIPOVEHICULO);
        db.execSQL(InvitacionesData.CREATE_TABLE);
        db.execSQL(SensoresData.CREATE_TABLE);
        db.execSQL(SensoresData.INSERT_SENSORES);

        Log.d("BaseDatos", "La base de datos fue creada");
     //   AutomovilData autoData = new AutomovilData(context);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + UsuarioData.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + VehiculoData.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + CatalogoMarcas.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + CatalogoModelos.TABLE_NAME);
        onCreate(db);

        Log.d("BaseDatos", "Fin Constructor");
    }

    public BaseDatos abrir(){
     //   miBase = new BaseDatos(contexto);
        // miBase.getWritableDatabase();
        db = this.getWritableDatabase();
        //   Toast.makeText(contexto, "Marcas: Base Abierta", Toast.LENGTH_SHORT).show();
        return this;
    }
    public void cerrar(){
        this.close();
        //   Toast.makeText(contexto, "Marcas: Base Cerrada", Toast.LENGTH_SHORT).show();
    }

}
