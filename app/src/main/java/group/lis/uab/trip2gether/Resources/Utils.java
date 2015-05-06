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
import java.util.concurrent.atomic.AtomicInteger;

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
     * Method setValueBBDD. Métode genéric per actualitzar un camp de tipus Boolean amb un objectId donat.
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
     * Method setValueBBDD. Métode genéric per actualitzar un camp de tipus String amb un objectId donat.
     * @param newValueString Valor a posar al <field> de la <table>
     * @param table Taula de la BBDD
     * @param field Camp de la <table> a actualizar.
     * @param objectId del registre a actualitzar.
     * @throws com.parse.ParseException
     */
    public static void setValueBBDD(String newValueString, String table, String field, String objectId) throws com.parse.ParseException {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("newValue", newValueString);
        params.put("table", table);
        params.put("field", field);
        params.put("objectId", objectId);
        ParseCloud.callFunction("setValueOfTableId", params);
    }

    /**
     * Method setValueBBDD. Métode genéric per actualitzar un camp de tipus Double amb un objectId donat.
     * @param newValueDouble Valor a posar al <field> de la <table>
     * @param table Taula de la BBDD
     * @param field Camp de la <table> a actualizar.
     * @param objectId del registre a actualitzar.
     * @throws com.parse.ParseException
     */
    public static void setValueBBDD(Double newValueDouble, String table, String field, String objectId) throws com.parse.ParseException {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("newValue", newValueDouble);
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

    /**
     * Method addNotification. Métode genéric per afegir una notificacio a la BBDD
     * segons uns parametres donats.
     * @param transmitterId camp Id_Emisor de Notificacion
     * @param receiverId camp Id_Receptor de Notificacion
     * @param clas camp Clas de Notificacion
     * @param type camp Tipo de Notificacion
     * @param idType camp Id_Tipo de Notificacion
     * @throws com.parse.ParseException
     */
    public static void addNotification(String transmitterId, String receiverId, String clas,String type, String idType) throws com.parse.ParseException {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("transmitterId", transmitterId);
        params.put("receiverId", receiverId);
        params.put("clas", clas);
        params.put("type", type);
        params.put("idType", idType);
        String a = ParseCloud.callFunction("addNotification", params);
    }

    /**
     * Method addFriendNotification. Métode genéric per afegir una notificacio Friend a la BBDD
     * segons uns parametres donats.
     * @param idViaje camp Tipo de Notificacion
     * @param idUsuario camp Id_Tipo de Notificacion
     * @throws com.parse.ParseException
     */
    public static void addGroupFriend(String idViaje, String idUsuario) throws com.parse.ParseException {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("Id_Viaje", idViaje);
        params.put("Id_Usuario", idUsuario);
        ParseCloud.callFunction("addGroupFriend", params);;
    }


    /**
     * Method getUserFromId. Métode que retorna el registre amb les dades del usuari demanat
     * segons uns parametres donats.
     * @param idUsuario camp objectId de la taula Usuario
     * @return retorna el List<ParseObject> que coincideixin amb el parametre donat.
     * @throws com.parse.ParseException
     */
    public static List<ParseObject> getUserFromId(String idUsuario) throws com.parse.ParseException {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("userId", idUsuario);
        return ParseCloud.callFunction("getUserFromId", params);
    }

    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);

    /**
     * Generate a value suitable for use in .
     * This value will not collide with ID values generated at build time by aapt for R.id.
     *
     * @return a generated ID value
     */
    public static int generateViewId() {
        for (;;) {
            final int result = sNextGeneratedId.get();
            // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
            int newValue = result + 1;
            if (newValue > 0x00FFFFFF) newValue = 1; // Roll over to 1, not 0.
            if (sNextGeneratedId.compareAndSet(result, newValue)) {
                return result;
            }
        }
    }
}

