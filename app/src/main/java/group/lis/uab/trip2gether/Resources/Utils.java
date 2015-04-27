package group.lis.uab.trip2gether.Resources;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import group.lis.uab.trip2gether.model.Trip;

public class Utils {

    /**
     * Auxiliars
     * @param string
     */
    public static void showInfoAlert(String string, Activity a)
    {
        String alertString = string; //missatge de alerta
        //Enviem el missatge dient que 's'ha inserit correctament
        new AlertDialog.Builder(a) //ens trobem en una funció de un botó, especifiquem la classe (no this)
                //.setTitle("DB")
                .setMessage(alertString)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //no fem res
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_info)
                .show();
    }

    /**
     * Method getRegistersFromBBDD. Métode genéric per obtenir una llista dels registres de la BBDD
     * segons uns parametres donats.
     * @param valueFieldTable Valor del <field> de la <table>
     * @param table Taula de la BBDD
     * @param field Camp de la <table>
     * @return List<ParseObject>
     * @throws com.parse.ParseException
     */
    public static List<ParseObject> getRegistersFromBBDD(String valueFieldTable, String table, String field) throws com.parse.ParseException {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("valueFieldTable", valueFieldTable);
        params.put("table", table);
        params.put("field", field);
        return ParseCloud.callFunction("getId", params);
    }

    /**
     * Method setValueBBDD. Métode genéric per actualitzar un camp amb un objectId donat.
     * @param newValueBoolean Valor a posar al <field> de la <table>
     * @param table Taula de la BBDD
     * @param field Camp de la <table> a actualizar.
     * @param objectId del registre a actualitzar.
     * @throws com.parse.ParseException
     */
    public static void setValueBBDD(Boolean newValueBoolean, String table, String field, String objectId) throws com.parse.ParseException {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("newValue", newValueBoolean);
        params.put("table", table);
        params.put("field", field);
        params.put("objectId", objectId);
        ParseCloud.callFunction("setValueOfTableId", params);
    }

    /**
     * Method getFriendsOfTrip. Retorna el número de amics que participen en un viatje
     * @param trip Viatje
     * @return getFriends Número de amics
     * @throws com.parse.ParseException
     */
    public static int getFriendsOfTrip(Trip trip) throws ParseException {
        int getFriends = 0;
        List<ParseObject> idsViaje = Utils.getRegistersFromBBDD(trip.getId(), "Grupo", "Id_Viaje");
        if(!idsViaje.isEmpty()){
            getFriends = idsViaje.size();
        }
        return getFriends;
    }

    /**
     * Method getSiteOfTrip. Retorna el número de llocs associats a en un viatje
     * @param trip Viatje
     * @return getFriends Número de llocs
     * @throws com.parse.ParseException
     */
    public static int getSiteOfTrip(Trip trip) throws ParseException {
        int getSites = 0;
        List<ParseObject> idsViaje = Utils.getRegistersFromBBDD(trip.getId(), "Sitio", "Id_Viaje");
        if(!idsViaje.isEmpty()){
            getSites = idsViaje.size();
        }
        return getSites;
    }

    /**
     * Mtehod convertFormatDate. Transforma el tipus data en un String operable per mostrar-ho per pantalla
     * Exemple de format resultant: Mon, Mar 9 - Mon, Mar16
     * @param date
     * @return resultat
     */
    public static String convertFormatDate(Date date){
        int espacios = 0;
        int i = 0;
        String result = "";
        String sdate = date.toString();
        while(espacios<3 && i<=sdate.length()){
            char c = sdate.charAt(i);
            if(c == ' '){
                espacios++;
            }
            result += c;
            i++;
        }
        return result;
    }
}