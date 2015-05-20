package group.lis.uab.trip2gether.controller;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
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
import group.lis.uab.trip2gether.model.Notification;
import group.lis.uab.trip2gether.model.NotificationListAdapter;
import group.lis.uab.trip2gether.model.Site;
import group.lis.uab.trip2gether.model.SiteListAdapter;
import group.lis.uab.trip2gether.model.TripListAdapter;
import group.lis.uab.trip2gether.model.User;

/**
 * Created by Jofré on 26/04/2015.
 */
public class NotificationList extends ActionBarActivity {
    private ArrayList<Notification> notifications = new ArrayList<Notification>();
    private ArrayList<String> notificationsNames = new ArrayList<String>();
    protected ListView lista;
    private static Context context = null;
    private Toolbar mToolbar;
    private ListView leftDrawerList;
    private DrawerLayout mDrawerLayout;
    private SmoothActionBarDrawerToggle mDrawerToggle;
    private ArrayAdapter<String> navigationDrawerAdapter;
    User myUser;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_list);
        context = this;
        Intent intent = getIntent();
        myUser = (User) intent.getSerializableExtra("myUser");

        setRef();
        //Set the custom toolbar
        setSupportActionBar(mToolbar);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new SmoothActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.open, R.string.close);
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        try {
            this.ViewNotificationsFromBBDD();
        } catch (com.parse.ParseException e) {
            e.printStackTrace();
        }
        this.setUserNotificationsViewed(); //al final les posem a vistes
    }

    private class SmoothActionBarDrawerToggle extends ActionBarDrawerToggle {

        private Runnable runnable;

        public SmoothActionBarDrawerToggle(NotificationList activity, DrawerLayout drawerLayout, Toolbar toolbar, int openDrawerContentDescRes, int closeDrawerContentDescRes) {
            super(activity, drawerLayout, toolbar, openDrawerContentDescRes, closeDrawerContentDescRes);
        }

        @Override
        public void onDrawerOpened(View drawerView) {
            super.onDrawerOpened(drawerView);
            invalidateOptionsMenu();
        }
        @Override
        public void onDrawerClosed(View view) {
            super.onDrawerClosed(view);
            invalidateOptionsMenu();
        }
        @Override
        public void onDrawerStateChanged(int newState) {
            super.onDrawerStateChanged(newState);
            if (runnable != null && newState == DrawerLayout.STATE_IDLE) {
                runnable.run();
                runnable = null;
            }
        }

        public void runWhenIdle(Runnable runnable) {
            this.runnable = runnable;
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    private void setRef()
    {
        mToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.action_bar_notification_list);
        leftDrawerList = (ListView) findViewById(R.id.left_drawer);

        View list_header = getLayoutInflater().inflate(R.layout.drawerlist_header, null);
        leftDrawerList.addHeaderView(list_header);
        String [] options = getResources().getStringArray(R.array.options_array);
        navigationDrawerAdapter = new ArrayAdapter<String>(NotificationList.this, R.layout.drawer_list_item, options);
        leftDrawerList.setAdapter(navigationDrawerAdapter);
        leftDrawerList.setOnItemClickListener(new DrawerItemClickListener());
    }

    public void setUserNotificationsViewed() //per posar les notificacions d'un usuari amb Estado "true" (vistes)
    {
        ///////QUERY NOTIS/////////////////////
        List<ParseObject> notiResponse = null; //crida al BE
        HashMap<String, Object> paramsQuery = new HashMap<String, Object>();
        paramsQuery.put("userId", myUser.getObjectId()); //(receptor)

        try {
            notiResponse = ParseCloud.callFunction("getActiveNotifications", paramsQuery);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //////////////////////////////////////////////////////////
        ///////UPDATE SITE/////////////////////
        for(int i = 0; i < notiResponse.size(); i++)
        {
            ParseObject noti = notiResponse.get(i);
            HashMap<String, Object> paramsQuery2 = new HashMap<String, Object>();
            paramsQuery2.put("objectId", noti.getObjectId()); //(receptor)
            if (noti.getString("Tipo").compareTo("Amistad")!=0) {
                try {
                    ParseCloud.callFunction("updateNotification", paramsQuery2);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            //////////////////////////////////////////////////////////
        }
    }

    public void ViewNotificationsFromBBDD() throws com.parse.ParseException {
        List<ParseObject> idsNotificacion = Utils.getRegistersFromBBDD(myUser.getObjectId(), "Notificacion", "Id_Receptor");

        for(int i=0;i<idsNotificacion.size();i++){
            ParseObject idNotificacion = idsNotificacion.get(i);
            Notification notificacion = new Notification(idNotificacion.getObjectId(), idNotificacion.getBoolean("Estado"),
                    idNotificacion.getString("Id_Emisor"), idNotificacion.getString("Id_Receptor"), idNotificacion.getString("Id_Tipo"),
                    idNotificacion.getString("Tipo"));
            if (!notificacion.getEstado()) {
                this.notifications.add(notificacion);
                this.notificationsNames.add(notificacion.getIdEmisor());
            }
        }

        NotificationListAdapter adaptador = new NotificationListAdapter(context, R.layout.notification_list_item_row, notifications, myUser);
        lista = (ListView)findViewById(R.id.listaNotificaciones);
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
    /**
     * Method DrawerItemClickListener
     */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        /**
         * Method onItemClick
         * @param parent
         * @param view
         * @param position
         * @param id
         */
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            switch (position) {
                case 1: {
                    mDrawerToggle.runWhenIdle(new Runnable() {
                        @Override
                        public void run() {
                            openMyProfile();
                        }
                    });
                    break;
                }
                case 2: {
                    mDrawerToggle.runWhenIdle(new Runnable() {
                        @Override
                        public void run() {
                            openMyTrips();
                        }
                    });
                    break;
                }
                case 3: {
                    mDrawerToggle.runWhenIdle(new Runnable() {
                        @Override
                        public void run() {
                            openFriends();
                        }
                    });
                    break;
                }
                case 4: {
                    mDrawerToggle.runWhenIdle(new Runnable() {
                        @Override
                        public void run() {
                            openNotificationList();
                        }
                    });
                    break;
                }
                case 5: {
                    mDrawerToggle.runWhenIdle(new Runnable() {
                        @Override
                        public void run() {
                            openSettings();
                        }
                    });
                    break;
                }
                case 6: {
                    mDrawerToggle.runWhenIdle(new Runnable() {
                        @Override
                        public void run() {
                            logout();
                        }
                    });
                    break;
                }
            }
            mDrawerLayout.closeDrawers();
        }
    }

    public void logout()
    {
        Intent i = new Intent(this, MainLaunchLogin.class);
        startActivity(i);
    }

    public void openSettings()
    {
        Intent i = new Intent(this, Settings.class);
        startActivity(i);
    }

    /**
     * Method openMyProfile
     */
    public void openMyProfile() {
        Intent userProfile = new Intent(this, UserProfile.class);
        userProfile.putExtra("myUser", myUser);
        startActivity(userProfile);
    }

    public void openMyTrips() {
        Intent myTrips = new Intent(this, TripList.class);
        myTrips.putExtra("myUser", myUser);
        startActivity(myTrips);
    }

    public void openNotificationList() {
        Intent notificationList = new Intent(this, NotificationList.class);
        notificationList.putExtra("myUser", myUser);
        startActivity(notificationList);
    }

    public void openFriends() {
        Intent friendsList = new Intent(this, Friends.class);
        friendsList.putExtra("myUser", myUser);
        startActivity(friendsList);
    }
}
