package group.lis.uab.trip2gether.controller;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import java.util.HashMap;
import java.util.List;
import group.lis.uab.trip2gether.R;
import group.lis.uab.trip2gether.Resources.Utils;
import group.lis.uab.trip2gether.model.User;

public class AddFriend extends ActionBarActivity {

    private Toolbar mToolbar;
    private User myUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend/**/);
        mToolbar = (Toolbar) findViewById(R.id.action_bar_add_friend);
        setSupportActionBar(mToolbar);
        Intent intent = getIntent();
        myUser = (User) intent.getSerializableExtra("myUser");
        this.initializeButtons();
    }

    /**
     * Method initializeButtons. Interfície
     */
    public void initializeButtons(){
        Button addFriend = (Button) findViewById(R.id.addFriend);
        addFriend.setOnClickListener(clickAddFriend);
    }

    public Button.OnClickListener clickAddFriend = new Button.OnClickListener() {
        public void onClick(View v) {
        int sendNotification;
        try {
            sendNotification = addFriend(AddFriend.this.getFriendMail());
            switch (sendNotification) {
                case 0:
                    String toastText = getResources().getString(R.string.sentFriendRequest) + " " + AddFriend.this.getFriendMail();
                    Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                case 1:
                    Toast.makeText(getApplicationContext(),  getResources().getString(R.string.userNotExist), Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.userAreYou), Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.friendShipExisting), Toast.LENGTH_SHORT).show();
                    break;
                case 4:
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.notificationAlreadySent), Toast.LENGTH_SHORT).show();
                    break;
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
        }else {
            mDrawerLayout.closeDrawer(Gravity.LEFT);
        }
        }
    };

    /**
     * Method addFriend
     * @param mail
     * @return
     * @throws ParseException
     */
    public int addFriend(String mail) throws ParseException {
        int success = 1;
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("mail", mail);
        params.put("transmitterId", myUser.getObjectId());
        params.put("type", "Amistad");
        params.put("status", false);
        //Comprovem si el mail existeix a la BD
        List<ParseObject> checkRegistro = ParseCloud.callFunction("checkUserSignIn", params);
        if (checkRegistro.isEmpty() == false){//mail existente
            //Comprovem que aquest no és el nostre mail
            ParseObject idFriend = checkRegistro.get(0);
            if (idFriend.getString("Mail").compareTo(myUser.getMail())==0) {
                success = 2;
                return success;
            }
            //Si existeix el mail comprovem que l'amistad entre usuaris no existeix

            List<ParseObject> checkAmistad = Utils.getRegistersFromBBDD(idFriend.getObjectId(), "Amistad", "Id_Usuario_2");
            for(int i=0;i<checkAmistad.size();i++){
                ParseObject idAmistad = checkAmistad.get(i);
                if (idAmistad.getString("Id_Usuario_1").compareTo(myUser.getObjectId())==0) {
                    success = 3;
                    return success;
                }
            }
            //Si no existeix comprovem que no s'hagi enviat ja una notificació a aquest usuari, o ell
            //ens l'hagi enviat a nosaltres
            List<ParseObject> checkNotificacion1 = Utils.getRegistersFromBBDD(idFriend.getObjectId(), "Notificacion", "Id_Emisor");
            for(int i=0;i<checkNotificacion1.size();i++){
                ParseObject idNotificacion1 = checkNotificacion1.get(i);
                if (idNotificacion1.getString("Id_Receptor").compareTo(myUser.getObjectId())==0 &&
                        idNotificacion1.getString("Tipo").compareTo("Amistad")==0 && !idNotificacion1.getBoolean("Estado")) {
                    success = 4;
                    return success;
                }
            }
            List<ParseObject> checkNotificacion2 = Utils.getRegistersFromBBDD(idFriend.getObjectId(), "Notificacion", "Id_Receptor");
            for(int i=0;i<checkNotificacion2.size();i++){
                ParseObject idNotificacion2 = checkNotificacion2.get(i);
                if (idNotificacion2.getString("Id_Emisor").compareTo(myUser.getObjectId())==0 &&
                        idNotificacion2.getString("Tipo").compareTo("Amistad")==0 && !idNotificacion2.getBoolean("Estado")) {
                    success = 4;
                    return success;
                }
            }
            //Creem la notificació d'amistad cap a l'usuari receptor
            ParseObject userParse = checkRegistro.iterator().next();
            params.put("receiverId", userParse.getObjectId());
            String sendNotificationResponse = ParseCloud.callFunction("sendFriendNotification", params); //crida al BE
            if(sendNotificationResponse.isEmpty() == false) //S'ha creat l'usuari
                success = 0;
            Log.i("Notification objectId:", sendNotificationResponse);
            return success;
        } else {
            return success;
        }
    }

    /**
     * Method getFriendMail
     * @return friendMailText
     */
    public String getFriendMail() {
        EditText friendMail = (EditText) findViewById(R.id.mail_friend);
        String friendMailText = friendMail.getText().toString();
        return friendMailText;
    }
}