package group.lis.uab.trip2gether.controller;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.lang.Runnable;
import group.lis.uab.trip2gether.R;
import group.lis.uab.trip2gether.Resources.Utils;
import group.lis.uab.trip2gether.model.Trip;
import group.lis.uab.trip2gether.model.TripListAdapter;
import group.lis.uab.trip2gether.model.User;

import android.support.v7.app.ActionBarDrawerToggle;

public class TripList extends ActionBarActivity {

    protected ListAdapter adapter;
    protected ListView lista;
    private static Context context;
    private static Intent intent;
    private User myUser;
    private ArrayList<Trip> trips = new ArrayList<Trip>();
    private Toolbar mToolbar;
    private ListView leftDrawerList;
    private DrawerLayout mDrawerLayout;
    private SmoothActionBarDrawerToggle mDrawerToggle;
    private ArrayAdapter<String> navigationDrawerAdapter;

    /**
     * Method onCreate
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_list);
        context = this;
        intent = this.getIntent();

        setRef();
        setSupportActionBar(mToolbar);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new SmoothActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.open, R.string.close);
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        myUser = (User) intent.getSerializableExtra("myUser");
        this.checkNotifications(); //per canviar el botó

        try {
            boolean thereAreTrips = this.ViewTripFromBBDD();
            if (!thereAreTrips)
            {
                setContentView(R.layout.no_element_layout);
                mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
                mDrawerToggle = new SmoothActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.open, R.string.close);
                mDrawerLayout.setDrawerListener(mDrawerToggle);

                context = this;
                intent = this.getIntent();

                setRef();
                setSupportActionBar(mToolbar);
                myUser = (User) intent.getSerializableExtra("myUser");
                this.checkNotifications(); //per canviar el botó
            }
        } catch (com.parse.ParseException e) {
            e.printStackTrace();
        }
    }

    private class SmoothActionBarDrawerToggle extends ActionBarDrawerToggle {

        private Runnable runnable;

        public SmoothActionBarDrawerToggle(TripList activity, DrawerLayout drawerLayout, Toolbar toolbar, int openDrawerContentDescRes, int closeDrawerContentDescRes) {
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
        mToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.action_bar_trip_list);
        leftDrawerList = (ListView) findViewById(R.id.left_drawer);

        View list_header = getLayoutInflater().inflate(R.layout.drawerlist_header, null);
        leftDrawerList.addHeaderView(list_header);
        String [] options = getResources().getStringArray(R.array.options_array);
        navigationDrawerAdapter = new ArrayAdapter<String>(TripList.this, R.layout.drawer_list_item, options);
        leftDrawerList.setAdapter(navigationDrawerAdapter);
        leftDrawerList.setOnItemClickListener(new DrawerItemClickListener());
    }

    /**
     * Refresh notifications too
     */
    @Override
    public void onResume() {
        super.onResume();
        this.checkNotifications();
    }

    public void checkNotifications()
    {
        //Query friends
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("userId", myUser.getObjectId());

        List<ParseObject> notiResponse = null;
        try {
            notiResponse = ParseCloud.callFunction("getActiveNotifications", params);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //Number of active notifications
        int notifications = notiResponse.size();
        if(notifications > 0)
        {
            mToolbar.setNavigationIcon(R.drawable.ic_action_noti);
        }
    }

    /**
     * Method ViewTripFromBBDD. Métode per visualitzar els viatjes associats a un usuari en la llista TripList
     * @throws com.parse.ParseException
     */
    public boolean ViewTripFromBBDD() throws com.parse.ParseException {
        boolean thereAreTrips = false;
        List<ParseObject> idsViatje = Utils.getRegistersFromBBDD(myUser.getObjectId(), "Grupo", "Id_Usuario");
        int len = idsViatje.size();
        if(!idsViatje.isEmpty()) {
            thereAreTrips = true;
            for(int i=0;i<len;i++){
                String idViaje = idsViatje.get(i).getString("Id_Viaje");

                List<ParseObject> getId = Utils.getRegistersFromBBDD(idViaje, "Viaje", "objectId");
                ParseObject camposViaje = getId.iterator().next();

                String idCiudad = camposViaje.getString("Id_Ciudad");
                List<ParseObject> datosIdCiudad = Utils.getRegistersFromBBDD(idCiudad, "Ciudad", "objectId");

                String pais = datosIdCiudad.get(0).getString("Pais");
                String ciudad = datosIdCiudad.get(0).getString("Nombre");

                Trip trip = new Trip(camposViaje.getString("Nombre"), pais,
                        ciudad, camposViaje.getDate("Fecha_Inicial"),
                        camposViaje.getDate("Fecha_Final"), camposViaje.getParseFile("Imagen"));
                trip.setId(idViaje);
                trips.add(trip);
            }
        }

        TripListAdapter adaptador = new TripListAdapter(this, R.layout.trip_list_item_row, trips, myUser);
        lista = (ListView)findViewById(R.id.listaViajes);
        lista.setAdapter(adaptador);

        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent (TripList.this, SiteList.class);
                intent.putExtra("id_viaje", trips.get(position).getId());
                intent.putExtra("nombre_viaje", trips.get(position).getNombre());
                intent.putExtra("myUser", myUser);
                startActivity(intent);
            }
        });

        return thereAreTrips;
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
        //ja estem als viatges, només tanquem la drawer
        mDrawerLayout.closeDrawers();
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


    /**
     * Method onCreateOptionsMenu. Barra superior
     * @param menu
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_trip_list, menu);
        return super.onCreateOptionsMenu(menu);
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
            case R.id.addTrip:
                Intent newTrip = new Intent(this, NewTripForm.class);
                newTrip.putExtra("myUser", myUser);
                startActivity(newTrip);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed()
    {
        moveTaskToBack(true);
    }
}