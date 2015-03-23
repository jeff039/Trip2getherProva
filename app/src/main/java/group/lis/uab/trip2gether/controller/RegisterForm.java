package group.lis.uab.trip2gether.controller;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.parse.ParseCloud;
import com.parse.ParseException;

import java.util.ArrayList;
import java.util.Date;
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
                        RegisterForm.this.getMail(),RegisterForm.this.getPassword());

            if(register) {
                Intent mainLaunchLogin = new Intent(RegisterForm.this, MainLaunchLogin.class);
                startActivity(mainLaunchLogin);
            }
            else {
                //TODO
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        }
    };

    ////////////////REGISTER////////////////////////////////////////
    public boolean register(String name, String surname, String city, String mail, String password) throws ParseException {
        boolean success = false;
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("name", name);
        params.put("surname", surname);
        params.put("city", city);
        params.put("mail", mail);
        params.put("password", password);

        ArrayList checkRegistro = ParseCloud.callFunction("checkUserSignIn", params);

        if (checkRegistro.size() == 1){//mail existente
            return success;
        }
        else {
            ArrayList registerResponse = ParseCloud.callFunction("register", params); //crida al BE
            if (registerResponse.size() == 1) //tenim un usuari
                success = true;
            System.out.println(success);
            return success;
        }
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

