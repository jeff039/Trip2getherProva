package group.lis.uab.trip2gether.controller;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import group.lis.uab.trip2gether.R;
import group.lis.uab.trip2gether.Resources.Utils;
import group.lis.uab.trip2gether.model.DrawerItemClickListener;
import group.lis.uab.trip2gether.model.FriendsListAdapter;
import group.lis.uab.trip2gether.model.Notification;
import group.lis.uab.trip2gether.model.NotificationListAdapter;
import group.lis.uab.trip2gether.model.Site;
import group.lis.uab.trip2gether.model.SiteListAdapter;
import group.lis.uab.trip2gether.model.TripListAdapter;
import group.lis.uab.trip2gether.model.User;

/**
 * Created by Jofré on 26/04/2015.
 */
public class Friends extends ActionBarActivity {
    private ArrayList<User> friends = new ArrayList<User>();
    private ArrayList<String> friendsNames = new ArrayList<String>();
    protected ListView lista;
    private static Context context = null;
    private Toolbar mToolbar;
    private ListView leftDrawerList;
    User myUser;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        context = this;
        Intent intent = getIntent();
        myUser = (User) intent.getSerializableExtra("myUser");

        mToolbar = (Toolbar) findViewById(R.id.action_bar_friends);
        setSupportActionBar(mToolbar);

        this.initializeDrawerLayout();
        this.initializeButtons();
        try {
            this.ViewFriendsFromBBDD();
        } catch (com.parse.ParseException e) {
            e.printStackTrace();
        }
    }

    public void ViewFriendsFromBBDD() throws com.parse.ParseException {
        List<ParseObject> idsFriends = Utils.getRegistersFromBBDD(myUser.getObjectId(), "Amistad", "Id_Usuario_1");

        for(int i=0;i<idsFriends.size();i++){
            ParseObject idFriends = idsFriends.get(i);
            List<ParseObject> friends = Utils.getRegistersFromBBDD(idFriends.getString("Id_Usuario_2"), "Usuario", "objectId");
            ParseObject idFriend = friends.get(0);
            User friend = new User(idFriend.getString("Mail"), idFriend.getString("Password"),
                    idFriend.getString("Nombre"), idFriend.getString("Apellidos"),
                    idFriend.getString("Pais"), idFriend.getString("Ciudad"), idFriend.getDate("Fecha_Nacimiento"),
                    idFriend.getObjectId());
                this.friends.add(friend);
                this.friendsNames.add(friend.getMail());

        }

        FriendsListAdapter adaptador = new FriendsListAdapter(context, R.layout.friends_item_row, friends, myUser);
        lista = (ListView)findViewById(R.id.listaAmigos);
        lista.setAdapter(adaptador);

        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /*Intent intent = new Intent (SiteList.this, SiteView.class);
                intent.putExtra("currentSite", sites.get(position));
                startActivity(intent);*/
            }
        });
    }

    /**
     * Method initializeButtons. Elements de la interfície
     */
    public void initializeButtons() {
        ImageButton openDrawer = (ImageButton) findViewById(R.id.openDrawer);
        openDrawer.setOnClickListener(clickDrawer);
    }

    /**
     * Method Button.OnClickListener clickDrawer
     */
    public Button.OnClickListener clickDrawer = new Button.OnClickListener() {
        public void onClick(View v) {
            DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
            if(!mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
                mDrawerLayout.openDrawer(Gravity.LEFT);
            }
            else {
                mDrawerLayout.closeDrawer(Gravity.LEFT);
            }
        }
    };

    /**
     * Method initializeDrawerLayout. Drawer layout
     */
    public void initializeDrawerLayout(){
        leftDrawerList = (ListView) findViewById(R.id.left_drawer);
        View list_header = getLayoutInflater().inflate(R.layout.drawerlist_header, null);
        leftDrawerList.addHeaderView(list_header);

        ListView mDrawerList = (ListView) findViewById(R.id.left_drawer);
        String [] options = getResources().getStringArray(R.array.options_array);
        mDrawerList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, options));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
    }

    /**
     * Method onCreateOptionsMenu. Action Bar
     * @param menu
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_launch_login, menu);
        return true;
    }
}