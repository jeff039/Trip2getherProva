package group.lis.uab.trip2gether.controller;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseCloud;
import com.parse.ParseException;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Formatter;
import java.util.HashMap;

import group.lis.uab.trip2gether.R;

/**
 * Created by Jofré on 18/03/2015.
 */
public class RegisterForm extends ActionBarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_form);
        this.initializeButtons();
    }

    /////////////////INTERFÍCIE////////////////////////////////////
    public void initializeButtons() {
        Button sendRegister = (Button)findViewById(R.id.sendRegister);
        sendRegister.setOnClickListener(clickSendRegister);
    }

    public Button.OnClickListener clickSendRegister = new Button.OnClickListener() {
        public void onClick(View v) {
            try {
                boolean register = RegisterForm.this.register(RegisterForm.this.getName(),
                        RegisterForm.this.getSurname(),RegisterForm.this.getCity(),
                        RegisterForm.this.getMail(),RegisterForm.this.getPassword(),
                        RegisterForm.this.getCountry());

            if(register) {
                Toast.makeText(getApplicationContext(), "Registration completed", Toast.LENGTH_SHORT).show();
                Intent mainLaunchLogin = new Intent(RegisterForm.this, MainLaunchLogin.class);
                startActivity(mainLaunchLogin);
            }
            else {
                Toast.makeText(getApplicationContext(), "Error registering the user", Toast.LENGTH_SHORT).show();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        }
    };

    ////////////////REGISTER////////////////////////////////////////
    public boolean register(String name, String surname, String city, String mail, String password,
                            String country) throws ParseException {
        boolean success = false;
        password = RegisterForm.encryptPassword(password);
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("name", name);
        params.put("surname", surname);
        params.put("city", city);
        params.put("mail", mail);
        params.put("password", password);
        params.put("country", country);

        String registerResponse = ParseCloud.callFunction("register", params); //crida al BE
        if(registerResponse.isEmpty() == false) //S'ha creat l'usuari
            success = true;
        Log.i("Registration objectId:", registerResponse);
        return success;
    }

    public String getName() {
        EditText name = (EditText) findViewById(R.id.name);
        String nameText = name.getText().toString();
        return nameText;
    }

    public String getSurname() {
        EditText surname = (EditText) findViewById(R.id.surname);
        String surnameText = surname.getText().toString();
        return surnameText;
    }

    public String getCity() {
        EditText city = (EditText) findViewById(R.id.city);
        String cityText = city.getText().toString();
        return cityText;
    }

    //public Date getDateOfBirth() {
    //EditText dateOfBirth = (EditText) findViewById(R.id.date_of_birth);
    //String dateOfBirthText = dateOfBirth.getText().
    //}

    public String getMail() {
        EditText mail = (EditText)findViewById(R.id.mail);
        String mailText = mail.getText().toString();
        return mailText;
    }

    public String getPassword() {
        EditText password = (EditText)findViewById(R.id.password);
        String passwordText = password.getText().toString();
        return passwordText;
    }

    public String getCountry() {
        EditText country = (EditText)findViewById(R.id.country);
        String countryText = country.getText().toString();
        return countryText;
    }

    private static String encryptPassword(String password)
    {
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

    private static String byteToHex(final byte[] hash)
    {
        Formatter formatter = new Formatter();
        for (byte b : hash)
        {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }

    ////////////////////////BARRA SUPERIOR////////////////////////////////////
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

}

