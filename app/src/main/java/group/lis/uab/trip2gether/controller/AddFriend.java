package group.lis.uab.trip2gether.controller;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import group.lis.uab.trip2gether.R;
import group.lis.uab.trip2gether.Resources.Encrypt;
import group.lis.uab.trip2gether.model.User;

/**
 * Created by Jofré on 20/04/2015.
 */
public class AddFriend extends ActionBarActivity{

    private User myUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        Intent intent = getIntent();
        myUser = (User) intent.getSerializableExtra("myUser");
        this.initializeButtons();

    }

    /////////////////////////////////INTERFICIE///////////////////////////////

    public void initializeButtons(){
        Button addFriend = (Button) findViewById(R.id.addFriend);
        addFriend.setOnClickListener(clickAddFriend);
    }

    public Button.OnClickListener clickAddFriend = new Button.OnClickListener() {
        public void onClick(View v) {
            boolean sendNotification = false; //CRIDEM EL MÈTODE LOGIN
            try {
                sendNotification = addFriend(AddFriend.this.getFriendMail());
                if(sendNotification) {
                    String toastText = "Enviada solicitud de amistad a "+AddFriend.this.getFriendMail();
                    Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_SHORT).show();
                    finish();
                }
                else {
                    Toast.makeText(getApplicationContext(), "El usuario no existe", Toast.LENGTH_SHORT).show();
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    };

    public Button.OnClickListener clickDrawer = new Button.OnClickListener() {
        public void onClick(View v) {
            DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
            if(!mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
                mDrawerLayout.openDrawer(Gravity.LEFT);
            }
            else
            {
                mDrawerLayout.closeDrawer(Gravity.LEFT);
            }
        }
    };

    /**
     * Method onCreateOptionsMenu. Barra superior
     * @param menu
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_profile, menu);

        return true;
    }

    /**
     * Method onOptionsItemSelected
     * @param item
     * @return true
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch(id) {
            case (R.id.edit_user_profile):
                Intent editUserForm = new Intent(this, EditUserForm.class);
                editUserForm.putExtra("myUser", myUser);
                startActivity(editUserForm);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /////////////////////////ADD FRIEND//////////////////////////////////////

    public boolean addFriend(String mail) throws ParseException {
        boolean success = false;
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("mail", mail);
        params.put("transmitterId", myUser.getObjectId());
        params.put("type", "Amistad");
        params.put("status", false);
        //Comprovem si el mail existeix a la BD
        List<ParseObject> checkRegistro = ParseCloud.callFunction("checkUserSignIn", params);
        if (checkRegistro.isEmpty() == false){//mail existente
            //Creem la notificació d'amistad cap a l'usuari receptor
            ParseObject userParse = checkRegistro.iterator().next();
            params.put("receiverId", userParse.getObjectId());
            String sendNotificationResponse = ParseCloud.callFunction("sendFriendNotification", params); //crida al BE
            if(sendNotificationResponse.isEmpty() == false) //S'ha creat l'usuari
                success = true;
            Log.i("Notification objectId:", sendNotificationResponse);
            return success;
        }
        else {
            return success;
        }
    }

    public String getFriendMail() {
        EditText friendMail = (EditText) findViewById(R.id.mail_friend);
        String friendMailText = friendMail.getText().toString();
        return friendMailText;
    }
}
