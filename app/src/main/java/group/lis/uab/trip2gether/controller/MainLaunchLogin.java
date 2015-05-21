package group.lis.uab.trip2gether.controller;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import java.util.HashMap;
import java.util.List;
import group.lis.uab.trip2gether.R;
import group.lis.uab.trip2gether.Resources.Utils;
import group.lis.uab.trip2gether.Resources.Encrypt;
import group.lis.uab.trip2gether.model.User;

public class MainLaunchLogin extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_launch_login);
        this.initializeButtons();
    }

    private void initializeButtons() {
        Button login = (Button)findViewById(R.id.loginButton);
        Button register = (Button)findViewById(R.id.registerButton);
        login.setOnClickListener(clickLogin);
        register.setOnClickListener(clickRegister);
    }

    public Button.OnClickListener clickLogin = new Button.OnClickListener() {
        public void onClick(View v) {
            if(!Utils.isNetworkStatusAvialable (getApplicationContext())) {
                Intent noInternet = new Intent(MainLaunchLogin.this, NoInternetConnection.class);
                startActivity(noInternet);
            }
            else {

                //POPUP DE LOGIN
                LayoutInflater layoutInflater
                        = (LayoutInflater) getBaseContext()
                        .getSystemService(LAYOUT_INFLATER_SERVICE);

                final View popupView = layoutInflater.inflate(R.layout.login_popup, null);
                final PopupWindow popupWindow = new PopupWindow(popupView);
                popupWindow.setFocusable(true);
                popupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
                popupWindow.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
                popupWindow.showAsDropDown(popupView);
                boolean login = false;
                try {
                    User myUser = MainLaunchLogin.this.login(MainLaunchLogin.this.getUser(),
                            MainLaunchLogin.this.getPassw());
                    if (myUser != null) {
                        Intent tripList = new Intent(MainLaunchLogin.this, TripList.class);
                        tripList.putExtra("myUser", myUser);
                        startActivity(tripList);
                    } else {
                        Utils.showInfoAlert(getResources().getString(R.string.loginErr), MainLaunchLogin.this);
                        popupWindow.dismiss();
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
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
     * Login.
     * @param user = mail
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

        List<ParseObject> loginResponse = ParseCloud.callFunction("login", params);
        if(!loginResponse.isEmpty()) {
            ParseObject userParse = loginResponse.iterator().next();
            myUser = new User(userParse.getString("Mail"), userParse.getString("Password"),
                    userParse.getString("Nombre"), userParse.getString("Apellidos"),
                    userParse.getString("Pais"), userParse.getString("Ciudad"),
                    userParse.getDate("Fecha_Nacimiento"), userParse.getObjectId());
        }
        return myUser;
    }

    /**
     * Method getUser
     * @return userText
     */
    public String getUser() {
        EditText user = (EditText) findViewById(R.id.user);
        String userText = user.getText().toString();
        return userText;
    }

    /**
     * Method getPassw
     * @return passwText
     */
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

    @Override
    public void onBackPressed()
    {
        moveTaskToBack(true);
    }
}