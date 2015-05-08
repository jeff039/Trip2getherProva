package group.lis.uab.trip2gether.Resources;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.Image;
import android.widget.ImageView;

import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import group.lis.uab.trip2gether.R;
import group.lis.uab.trip2gether.model.Trip;

public class Utils {

    /**
     * Method getCitiesOfCountry
     * @return ciudades
     * @throws ParseException
     */
    public static ArrayList<String> getCitiesOfCountry(String pais) throws ParseException {
        ArrayList<String> ciudades = new ArrayList<String>();
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("pais", pais);
        List<ParseObject> listaCiudades = ParseCloud.callFunction("getCitiesFromCountry", params);
        for(int i=0;i<listaCiudades.size();i++){
            ParseObject ciudad = listaCiudades.get(i);
            String nombreCiudad = ciudad.getString("Nombre");
            ciudades.add(nombreCiudad);
        }
        return ciudades;
    }

    /**
     * Method getCountriesOfBBDD
     * @return paises
     * @throws ParseException
     */
    public static ArrayList<String> getCountriesOfBBDD() throws ParseException {
        ArrayList<String> paises = new ArrayList<String>();
        HashMap<String, Object> params = new HashMap<String, Object>();
        List<ParseObject> listaPaises = ParseCloud.callFunction("getCountriesOfBBDD", params);
        for(int i=0;i<listaPaises.size();i++){
            ParseObject pais = listaPaises.get(i);
            String nombreCiudad = pais.getString("Pais");
            if(!paises.contains(nombreCiudad)) {
                paises.add(nombreCiudad);
            }
        }
        return paises;
    }

    /**
     * Method showInfoAlert
     * @param string
     * @param a
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

    /**
     * Función para redondear una imagen.
     *
     * @return output Bitmap de la imagen redondeada.
     */
    public static Bitmap getRoundedCornerBitmap( Bitmap image, boolean square) {
        int width = 0;
        int height = 0;

        Bitmap bitmap = image ;

        if(square){
            if(bitmap.getWidth() < bitmap.getHeight()){
                width = bitmap.getWidth();
                height = bitmap.getWidth();
            } else {
                width = bitmap.getHeight();
                height = bitmap.getHeight();
            }
        } else {
            height = bitmap.getHeight();
            width = bitmap.getWidth();
        }

        Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, width, height);
        final RectF rectF = new RectF(rect);
        final float roundPx = 90;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }


    /**
     * Función que carga el ParseFile (imagen) en el ImageView dado.
     * @param imageView ImageView donde se cargara la imagen.
     * @param file ParseFile de la a cargar.
     * @param rounded Boolean con o sin efecto de redonda.
     */
    public static void setImageViewWithParseFile(ImageView imageView, ParseFile file, Boolean rounded) {
        if (file != null) {
            byte[] bitmapdata = new byte[0];
            try {
                bitmapdata = file.getData();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            //Try to reduce the necessary memory
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            options.inSampleSize = 2;

            Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapdata, 0, bitmapdata.length, options);
            if(rounded) {
                imageView.setImageBitmap(getRoundedCornerBitmap(bitmap, rounded));
            }else{
                imageView.setImageBitmap(bitmap);
            }
        }
    }
}

