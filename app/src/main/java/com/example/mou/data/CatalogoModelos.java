package com.example.mou.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.example.mou.model.Modelo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mou on 04/07/2015.
 */
public class CatalogoModelos {

    private static final String CLMN_ID = "idModelo";
    private static final String CLMN_NOMBRE = "nombre";
    private static final String CLMN_ID_MARCA = "idMarca";

    public static final String TABLE_NAME = "modelos";

    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " ("
            +CLMN_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "
            +CLMN_NOMBRE+" TEXT, "
            +CLMN_ID_MARCA+" INTEGER"
            +");";
    public static final String ACURA = "INSERT INTO "+TABLE_NAME+" (nombre, idMarca) VALUES "
            +"('ARX',1),"+"('CSX',1),"+"('MDX',1),"+"('NSX',1),"+"('RDX',1),"+"('RSX',1)"
            +";";
    public static final String AUDI = "INSERT INTO "+TABLE_NAME+" (nombre, idMarca) VALUES "
            +"('A1',2),"+"('A2',2),"+"('A3',2),"+"('A5',2),"+"('A6',2),"+"('A8',2),"
            +"('Q1',2),"+"('Q3',2),"+"('Q5',2),"+"('Q7',2),"+"('QUATTRO',2),"+"('R8',2),"
            +"('TT',2)"
            +";";
    public static final String BMW = "INSERT INTO "+TABLE_NAME+" (nombre, idMarca) VALUES "
            +"('I8',3),"+"('SERIE 1',3),"+"('SERIE 2',3),"+"('SERIE 3',3),"+"('SERIE 4',3),"
            +"('SERIE 5',3),"+"('SERIE 6',3),"+"('SERIE 7',3),"+"('SERIE 8',3),"
            +"('X1',3),"+"('X3',3),"+"('X5',3),"+"('X6',3),"+"('Z1',3),"+"('Z2',3),"
            +"('Z3',3),"+"('Z4',3),"+"('Z8',3)"
            +";";
    public static final String CADILLAC = "INSERT INTO "+TABLE_NAME+" (nombre, idMarca) VALUES "
            +"('ATS',4),"+"('CTS',4),"+"('SRX',4),"+"('ESCALADE',4)"
            +";";
    public static final String CHEVROLET = "INSERT INTO "+TABLE_NAME+" (nombre, idMarca) VALUES "
            +"('MATIZ',5),"+"('SPARK',5),"+"('AVEO',5),"+"('SONIC',5),"+"('CRUZE',5),"
            +"('MALIBU',5),"+"('CAMARO',5),"+"('TRAX',5),"+"('CAPTIVA',5),"+"('TRAVERSE',5),"
            +"('TAHOE',5),"+"('SUBURBAN',5),"+"('TORNADO',5),"+"('CHEYENNE',5)"
            +";";
    public static final String CHRYSLER = "INSERT INTO "+TABLE_NAME+" (nombre, idMarca) VALUES "
            +"('200',6),"+"('300',6),"+"('SRT',6),"+"('TOWN AND COUNTRY',6)"
            +";";
    public static final String DODGE = "INSERT INTO "+TABLE_NAME+" (nombre, idMarca) VALUES "
            +"('DART',7),"+"('CHARGER',7),"+"('CHALLENGER',7),"+"('JOURNEY',7),"
            +"('DURANGO',7)"
            + ";";
    public static final String FIAT = "INSERT INTO "+TABLE_NAME+" (nombre, idMarca) VALUES "
            +"('DUCATO',8),"+"('PALIO',8),"+"('UNO',8),"+"('ABARTH',8),"+"('500',8)"
            +";";
    public static final String FORD = "INSERT INTO "+TABLE_NAME+" (nombre, idMarca) VALUES "
            +"('IKON',9),"+"('FIESTA',9),"+"('FOCUS',9),"+"('FUSION',9),"+"('MUSTANG',9),"
            +"('ECOSPORT',9),"+"('ESCAPE',9),"+"('EDGE',9),"+"('EXPLORER',9),"
            +"('EXPEDITION',9),"+"('RANGER',9),"+"('LOBO',9)"
            +";";
    public static final String GMC = "INSERT INTO "+TABLE_NAME+" (nombre, idMarca) VALUES "
            +"('TERRAIN',10),"+"('ACADIA',10),"+"('SIERRA',10),"+"('YUKON',10)"
            +";";
    public static final String HONDA = "INSERT INTO "+TABLE_NAME+" (nombre, idMarca) VALUES "
            +"('ACCORD',11),"+"('CIVIC',11),"+"('CROSSTOUR',11),"+"('CR-Z',11),"
            +"('FIT',11),"+"('CITY',11),"+"('HR-V',11),"+"('CR-V',11),"
            +"('ODYSSEY',11),"+"('PILOT',11),"+"('RIDGELINE',11)"
            +";";
    public static final String JEEP = "INSERT INTO "+TABLE_NAME+" (nombre, idMarca) VALUES "
            +"('CHEROKEE',12),"+"('PATRIOT',12),"+"('COMPASS',12),"+"('WRANGLER',12),"
            +"('GRAND CHEROKEE',12)"
            +";";
    public static final String KIA = "INSERT INTO "+TABLE_NAME+" (nombre, idMarca) VALUES "
            +"('FORTE',13),"+"('SPORTAGE',13),"+"('SORENTO',13)"
            +";";
    public static final String MAZDA = "INSERT INTO "+TABLE_NAME+" (nombre, idMarca) VALUES "
            +"('2',14),"+"('3',14),"+"('5',14),"+"('6',14),"+"('MX-5',14),"
            +"('CX-5',14),"+"('CX-9',14)"
            +";";
    public static final String MERCEDEZ = "INSERT INTO "+TABLE_NAME+" (nombre, idMarca) VALUES "
            +"('CLASE A',15),"+"('CLASE B',15),"+"('CLASE C',15),"+"('CLASE CLA',15),"
            +"('CLASE CLS',15),"+"('CLASE G',15),"+"('CLASE GL',15),"+"('CLASE GLA',15),"
            +"('CLASE GLK',15),"+"('CLASE GLE',15),"+"('CLASE M',15),"+"('CLASE S',15),"
            +"('CLASE SL',15),"+"('CLASE SLK',15),"+"('AMG GT',15),"+"('VIANO',15)"
            +";";
    public static final String NISSAN = "INSERT INTO "+TABLE_NAME+" (nombre, idMarca) VALUES "
            +"('TSURU',16),"+"('MARCH',16),"+"('TIIDA',16),"+"('NP300',16),"
            +"('NOTE',16),"+"('VERSA',16),"+"('SENTRA',16),"+"('ALTIMA',16),"
            +"('X-TRAIL',16),"+"('JUKE',16),"+"('FRONTIER',16),"+"('TITAN',16),"
            +"('PATHFINDER',16),"+"('MAXIMA',16),"+"('MURANO',16),"
            +"('370Z',16),"+"('LEAF',16),"+"('ARMADA',16)"
            +";";
    public static final String PEUGEOT = "INSERT INTO "+TABLE_NAME+" (nombre, idMarca) VALUES "
            +"('208',17),"+"('301',17),"+"('308',17),"+"('508',17),"+"('2008',17),"
            +"('3008',17),"+"('RCZ',17),"+"('PARTNER TEPEE',17),"+"('PARTNER MAXI',17)"
            +";";
    public static final String RENAULT = "INSERT INTO "+TABLE_NAME+" (nombre, idMarca) VALUES "
            +"('LOGAN',18),"+"('SENDERO',18),"+"('STEPWAY',18),"+"('FLUENCE',18),"
            +"('DUSTER',18),"+"('KOLEOS',18),"+"('SAFRANE',18),"+"('CLIO',18),"
            +"('KANGOO',18),"+"('TWIZY Z.E.',18)"
            +";";
    public static final String SEAT = "INSERT INTO "+TABLE_NAME+" (nombre, idMarca) VALUES "
            +"('IBIZA',19),"+"('LEÓN',19),"+"('TOLEDO',19)"
            +";";
    public static final String TOYOTA = "INSERT INTO "+TABLE_NAME+" (nombre, idMarca) VALUES "
            +"('AVANZA',20),"+"('CAMRY',20),"+"('COROLLA',20),"+"('YARIS',20),"
            +"('HIGHLANDER',20),"+"('LANDCRUISER',20),"+"('RAV4',20),"+"('SEQUOIA',20),"
            +"('SIENNA',20),"+"('TACOMA',20),"+"('TUNDRA',20),"+"('PRIUS',20)"
            +";";
    public static final String VOLKSWAGEN = "INSERT INTO "+TABLE_NAME+" (nombre, idMarca) VALUES "
            +"('TOUAGEG',21),"+"('VENTO',21),"+"('CROSSFOX',21),"+"('BEETLE',21),"
            +"('POLO',21),"+"('GOL',21),"+"('GOLF',21),"+"('JETTA',21),"+"('CC',21),"
            +"('PASSAT',21),"+"('TIGUAN',21)"
            +";";
    public static final String VOLVO = "INSERT INTO "+TABLE_NAME+" (nombre, idMarca) VALUES "
            +"('XC90',22),"+"('V60',22),"+"('S80',22),"+"('V40',22),"+"('XC60',22),"
            +"('S60',22)"
            +";";

    private SQLiteDatabase db;

//    public CatalogoModelos(Context context) {
//        this.contexto = context;
//    }

    public CatalogoModelos(BaseDatos miBase) {
        db = miBase.getWritableDatabase();
    }

    public List<Modelo> obtenerModelosPorMarca(Integer idMarca){
        List<Modelo> modelos = new ArrayList<Modelo>();
        Cursor cursor = db.rawQuery("SELECT * FROM "+TABLE_NAME+" WHERE "+CLMN_ID_MARCA+" = ? ORDER BY "+CLMN_NOMBRE,
                                                                                            new String[]{idMarca.toString()});

        while(cursor.moveToNext()){
            Modelo modelo = new Modelo();
            modelo.setIdModelo(cursor.getInt(cursor.getColumnIndex(CLMN_ID)));
            modelo.setNombre(cursor.getString(cursor.getColumnIndex(CLMN_NOMBRE)));
            modelo.setIdMarca(cursor.getInt(cursor.getColumnIndex(CLMN_ID_MARCA)));
            modelos.add(modelo);
        }
        cursor.close();
        return modelos;
    }

    public Modelo obtenerModeloPorId(Integer idModelo){
        if(idModelo!=null) {
            Modelo modelo = new Modelo();
            Cursor c = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + CLMN_ID + " =?", new String[]{idModelo.toString()});
            if (c.moveToFirst()) {
                modelo.setIdModelo(c.getInt(c.getColumnIndex(CLMN_ID)));
                modelo.setNombre(c.getString(c.getColumnIndex(CLMN_NOMBRE)));
                modelo.setIdMarca(c.getInt(c.getColumnIndex(CLMN_ID_MARCA)));
                c.close();
                return modelo;
            }
            c.close();
        }
        return null;
    }

}
