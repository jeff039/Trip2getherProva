package group.lis.uab.trip2gether.controller;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import group.lis.uab.trip2gether.R;
import group.lis.uab.trip2gether.model.Encrypt;
import group.lis.uab.trip2gether.model.User;

public class MainLaunchLogin extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_launch_login);
        this.initializeButtons();
        this.getSupportActionBar().hide();
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
                User myUser = MainLaunchLogin.this.login(MainLaunchLogin.this.getUser(),
                        MainLaunchLogin.this.getPassw());
                if(myUser != null)
                {
                    Intent tripList = new Intent(MainLaunchLogin.this, TripList.class);
                    tripList.putExtra("myUser", myUser);
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
    public User login(String user, String passw) throws ParseException {
        Encrypt encrypt = new Encrypt(getApplicationContext());
        User myUser = null;
        passw = encrypt.encryptPassword(passw);
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("user", user);
        params.put("passw", passw);

        List<ParseObject> loginResponse = ParseCloud.callFunction("login", params); //crida al BE
        if(loginResponse.isEmpty() == false) { //tenim un usuari
            ParseObject userParse = loginResponse.iterator().next();
            myUser = new User(userParse.getString("Mail"), userParse.getString("Password"),
                    userParse.getString("Nombre"), userParse.getString("Apellidos"),
                    userParse.getString("Pais"), userParse.getString("Ciudad"),
                    userParse.getDate("Fecha_Nacimiento"), userParse.getObjectId());
        }
        return myUser;
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
