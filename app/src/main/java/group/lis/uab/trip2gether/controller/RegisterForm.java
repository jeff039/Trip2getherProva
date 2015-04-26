package group.lis.uab.trip2gether.controller;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;
import android.app.ActionBar;

import com.parse.ParseCloud;
import com.parse.ParseException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import group.lis.uab.trip2gether.R;
import group.lis.uab.trip2gether.Resources.Encrypt;

/**
 * Created by Jofré on 18/03/2015.
 */
public class RegisterForm extends ActionBarActivity {

    //UI References
    private EditText pickDate;
    private DatePickerDialog pickDateDialog;
    private SimpleDateFormat dateFormatter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_form);
        this.initializeButtons();
        this.setDateTimeField();
    }

    /////////////////INTERFÍCIE////////////////////////////////////
    public void initializeButtons() {
        Button sendRegister = (Button)findViewById(R.id.sendRegister);
        sendRegister.setOnClickListener(clickSendRegister);
        pickDate = (EditText) findViewById(R.id.date_of_birth);
        pickDate.setOnClickListener(clickPickDate);
        pickDate.setInputType(InputType.TYPE_NULL);
        pickDate.setOnFocusChangeListener(focusPickDate);
        dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
    }

    public Button.OnClickListener clickSendRegister = new Button.OnClickListener() {
        public void onClick(View v) {
            try {
                boolean register = RegisterForm.this.register(RegisterForm.this.getName(),
                        RegisterForm.this.getSurname(),RegisterForm.this.getCity(),
                        RegisterForm.this.getMail(),RegisterForm.this.getPassword(),
                        RegisterForm.this.getCountry(), RegisterForm.this.getDateOfBirth());

            if(register) {
                Toast.makeText(getApplicationContext(), "Registration completed", Toast.LENGTH_SHORT).show();
                //Intent mainLaunchLogin = new Intent(RegisterForm.this, MainLaunchLogin.class);
                finish();
            }
            else {
                Toast.makeText(getApplicationContext(), "Error registering the user", Toast.LENGTH_SHORT).show();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (java.text.ParseException e) {
                e.printStackTrace();
            }
        }
    };

    public EditText.OnClickListener clickPickDate = new EditText.OnClickListener() {
        @Override
        public void onClick(View view) {
            pickDateDialog.show();
        }
    };

    public EditText.OnFocusChangeListener focusPickDate = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus)
                pickDateDialog.show();
            v.clearFocus();
        }
    };

    private void setDateTimeField() {
        Calendar newCalendar = Calendar.getInstance();
        pickDateDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                pickDate.setText(dateFormatter.format(newDate.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

    }



    ////////////////REGISTER////////////////////////////////////////
    public boolean register(String name, String surname, String city, String mail, String password,
                            String country, Date date_of_birth) throws ParseException {
        Encrypt encrypt = new Encrypt(getApplicationContext());
        boolean success = false;
        password = encrypt.encryptPassword(password);
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("name", name);
        params.put("surname", surname);
        params.put("city", city);
        params.put("mail", mail);
        params.put("password", password);
        params.put("country", country);
        params.put("date_of_birth", date_of_birth);

        //Comprovem si el mail existeix a la BD
        ArrayList checkRegistro = ParseCloud.callFunction("checkUserSignIn", params);
        if (checkRegistro.size() == 1){//mail existente
            return success;
        }
        else {
            //Creem el nou usuari a la BD
            String registerResponse = ParseCloud.callFunction("register", params); //crida al BE
            if(registerResponse.isEmpty() == false) //S'ha creat l'usuari
                success = true;
            Log.i("Registration objectId:", registerResponse);
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

    public Date getDateOfBirth() throws java.text.ParseException {
        dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
        EditText dateOfBirth = (EditText) findViewById(R.id.date_of_birth);
        Date dateOfBirthDate;
        dateOfBirthDate = dateFormatter.parse(dateOfBirth.getText().toString());
        dateOfBirthDate.setHours(13);
        return dateOfBirthDate;
    }

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
        EditText country = (EditText) findViewById(R.id.country);
        String countryText = country.getText().toString();
        return countryText;
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

