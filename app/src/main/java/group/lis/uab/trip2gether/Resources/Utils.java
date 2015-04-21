package group.lis.uab.trip2gether.Resources;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

/**
 * Created by davidcara on 21/4/15.
 */
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
}
