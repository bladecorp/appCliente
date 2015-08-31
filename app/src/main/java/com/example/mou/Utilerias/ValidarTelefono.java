package com.example.mou.Utilerias;

/**
 * Created by Mou on 10/07/2015.
 */
public class ValidarTelefono {

    public static boolean validar10digitos(String telefono){
        int numeros = 0;
        char[] cadena = telefono.toCharArray();
        for(char caracter : cadena){
            if(Character.isDigit(caracter)){
                numeros++;
            }
        }

        if(numeros == 10){
            return true;
        }
        return false;
    }

    public static String obtener10digitos(String telefono){
        String telefonoValido = "";
        char[] cadena = telefono.toCharArray();
        for(char caracter : cadena){
            if(Character.isDigit(caracter)){
                telefonoValido += caracter;
            }
        }
        return telefonoValido;
    }

}
