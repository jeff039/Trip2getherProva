package group.lis.uab.trip2gether.controller;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.parse.ParseCloud;
import com.parse.ParseException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.HashMap;
import group.lis.uab.trip2gether.R;

public class MainLaunchLogin extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_launch_login);
        this.initializeButtons();
    }

    /**
     * Interficie
     */
    private void initializeButtons() {
        Button login = (Button)findViewById(R.id.loginButton);
        Button register = (Button)findViewById(R.id.registerButton);
        login.setOnClickListener(clickLogin);
        register.setOnClickListener(clickRegister);
    }

    public Button.OnClickListener clickLogin = new Button.OnClickListener() {
        public void onClick(View v) {
            boolean login = false; //CRIDEM EL MÈTODE LOGIN
            try {
                login = MainLaunchLogin.this.login(MainLaunchLogin.this.getUser(),
                        MainLaunchLogin.this.getPassw());
                if(login)
                {
                    Intent tripList = new Intent(MainLaunchLogin.this, TripList.class);
                    startActivity(tripList);
                }else{
                    MainLaunchLogin.this.showInfoAlert(getResources().getString(R.string.loginErr));
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    };

    public Button.OnClickListener clickRegister = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent registerForm = new Intent(MainLaunchLogin.this, RegisterForm.class);
            startActivity(registerForm);
        }
    };

    /**
     * Login. User serà el email (VIGILAR NO EMAILS REPETITS)
     * @param user
     * @param passw
     * @return success
     * @throws ParseException
     */
    public boolean login(String user, String passw) throws ParseException {

        boolean success = false;
        passw = MainLaunchLogin.encryptPassword(passw);
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("user", user);
        params.put("passw", passw);

        ArrayList loginResponse = ParseCloud.callFunction("login", params); //crida al BE
        if(loginResponse.size() == 1) //tenim un usuari
            success = true;
        return success;
    }

    public String getUser() {
        EditText user = (EditText) findViewById(R.id.user);
        String userText = user.getText().toString();
        return userText;
    }

    public String getPassw() {
        EditText passw = (EditText) findViewById(R.id.passw);
        String passwText = passw.getText().toString();
        return passwText;
    }

    private static String encryptPassword(String password) {
        String sha1 = "";
        try
        {
            MessageDigest crypt = MessageDigest.getInstance("SHA-1");
            crypt.reset();
            crypt.update(password.getBytes("UTF-8"));
            sha1 = byteToHex(crypt.digest());
        }
        catch(NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        catch(UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        return sha1;
    }

    private static String byteToHex(final byte[] hash) {
        Formatter formatter = new Formatter();
        for (byte b : hash)
        {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }


    /**
     * Action Bar
     * @param menu
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_launch_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    /**
     * Auxiliars
     * @param string
     */
    public void showInfoAlert(String string)
    {
        String alertString = string; //missatge de alerta
        //Enviem el missatge dient que 's'ha inserit correctament
        new AlertDialog.Builder(MainLaunchLogin.this) //ens trobem en una funció de un botó, especifiquem la classe (no this)
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
