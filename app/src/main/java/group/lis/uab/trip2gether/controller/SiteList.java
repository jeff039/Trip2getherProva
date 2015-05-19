package group.lis.uab.trip2gether.controller;

import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.view.MenuItem;

import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import group.lis.uab.trip2gether.R;
import group.lis.uab.trip2gether.Resources.Utils;
import group.lis.uab.trip2gether.model.DrawerItemClickListener;
import group.lis.uab.trip2gether.model.Site;
import group.lis.uab.trip2gether.model.SiteListAdapter;
import group.lis.uab.trip2gether.model.User;

import android.support.v7.widget.Toolbar;
import android.widget.TextView;

public class SiteList  extends ActionBarActivity {

    private static Intent intent = null;
    private ArrayList<Site> sites = new ArrayList<Site>();
    protected  ListView lista;
    private static Context context = null;
    private String idViaje = "";
    private Toolbar mToolbar;
    private ListView leftDrawerList;
    private User myUser;
    private String nombreViaje = "";
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
        setContentView(R.layout.activity_site_list);
        context = this;
        intent = getIntent();
        idViaje = intent.getStringExtra("id_viaje");
        this.nombreViaje = intent.getStringExtra("nombre_viaje");
        TextView name = (TextView)findViewById(R.id.txtSiteName);
        name.setText(nombreViaje);

        setRef();
        //Set the custom toolbar
        setSupportActionBar(mToolbar);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new SmoothActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.open, R.string.close);
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        myUser = (User) intent.getSerializableExtra("myUser");

        this.checkNotifications();

        try {
            this.ViewSiteFromBBDD();
        } catch (com.parse.ParseException e) {
            e.printStackTrace();
        }
    }

    private class SmoothActionBarDrawerToggle extends ActionBarDrawerToggle {

        private Runnable runnable;

        public SmoothActionBarDrawerToggle(SiteList activity, DrawerLayout drawerLayout, Toolbar toolbar, int openDrawerContentDescRes, int closeDrawerContentDescRes) {
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
        mToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.action_bar_site_list);
        leftDrawerList = (ListView) findViewById(R.id.left_drawer);

        View list_header = getLayoutInflater().inflate(R.layout.drawerlist_header, null);
        leftDrawerList.addHeaderView(list_header);
        String [] options = getResources().getStringArray(R.array.options_array);
        navigationDrawerAdapter = new ArrayAdapter<String>(SiteList.this, R.layout.drawer_list_item, options);
        leftDrawerList.setAdapter(navigationDrawerAdapter);
        leftDrawerList.setOnItemClickListener(new DrawerItemClickListener());
    }

    @Override
    public void onResume() //quan recarreguem la vista també les notificacions
    {
        super.onResume();
        this.checkNotifications();
    }

    public void checkNotifications()
    {
        ///////QUERY AMICS/////////////////////
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("userId", myUser.getObjectId());

        List<ParseObject> notiResponse = null; //crida al BE
        try {
            notiResponse = ParseCloud.callFunction("getActiveNotifications", params);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int notifications = notiResponse.size(); //quantes notificacions actives
        ImageButton openDrawer = (ImageButton) findViewById(R.id.openDrawer);
        if(notifications > 0)
        {
            mToolbar.setNavigationIcon(R.drawable.ic_action_noti);
        }
    }

    /**
     * Method ViewSiteFromBBDD. Métode per visualitzar els Sites associats a un Vaitge en la llista SiteList
     * @throws com.parse.ParseException
     */
    public void ViewSiteFromBBDD() throws com.parse.ParseException {
        if(this.idViaje==null){
            this.idViaje="";
        }
        List<ParseObject> idsSitio = Utils.getRegistersFromBBDD(this.idViaje, "Sitio", "Id_Viaje");

        for(int i=0;i<idsSitio.size();i++){
            ParseObject idSitio = idsSitio.get(i);
            Site sitio = new Site(idSitio.getString("Nombre"), idSitio.getString("Descripcion"),
                    idSitio.getParseFile("Imagen"), idSitio.getString("Id_Viaje"), idSitio.getObjectId(), idSitio.getDouble("Duracion"),
                    idSitio.getDouble("Precio"), idSitio.getDouble("Latitud"), idSitio.getDouble("Longitud"));
            sitio.setEstrellas(idSitio.getDouble("Estrellas"));
            this.sites.add(sitio);
        }

        SiteListAdapter adaptador = new SiteListAdapter(context,R.layout.site_list_item_row, sites);
        lista = (ListView)findViewById(R.id.listaSitios);
        lista.setAdapter(adaptador);

        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent (SiteList.this, SiteView.class);
                intent.putExtra("currentSiteId", sites.get(position).getId().toString());
                intent.putExtra("myUser", myUser);
                intent.putExtra("nombre_viaje", nombreViaje);
                intent.putExtra("id_viaje", idViaje);
                startActivity(intent);
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
        getMenuInflater().inflate(R.menu.menu_site_list, menu);
        return true;
    }

    /**
     * Method onOptionsItemSelected
     * @param item
     * @return super.onOptionsItemSelected(item)
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == R.id.addSite)  {
            String tripId = this.idViaje;

            try {
                ParseQuery<ParseObject> tripCoordQuery = ParseQuery.getQuery("Viaje");
                tripCoordQuery.whereEqualTo("objectId", tripId);
                String cityId = tripCoordQuery.getFirst().getString("Id_Ciudad");
                ParseQuery<ParseObject> cityCoordQuery = ParseQuery.getQuery("Ciudad");
                cityCoordQuery.whereEqualTo("objectId", cityId);
                Double latitude = cityCoordQuery.getFirst().getDouble("Latitud");
                Double longitude = cityCoordQuery.getFirst().getDouble("Longitud");
                //enviem el viatge i la ubicació per fer focus
                Bundle paramsMaps = new Bundle();
                paramsMaps.putDouble("latitude", latitude);
                paramsMaps.putDouble("longitude", longitude);
                paramsMaps.putString("tripId", tripId);
                paramsMaps.putString("route", "false"); //estarem editant, no mirant la ruta
                paramsMaps.putSerializable("myUser", myUser);
                paramsMaps.putString("id_viaje", this.idViaje);
                paramsMaps.putString("nombre_viaje", this.nombreViaje);

                Intent siteMaps = new Intent(this, SiteMapsActivity.class);
                siteMaps.putExtras(paramsMaps);
                startActivity(siteMaps);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        if(id == R.id.mapRoute) {
            String tripId = this.idViaje;

            ParseQuery<ParseObject> siteCoordQuery = ParseQuery.getQuery("Sitio");
            siteCoordQuery.whereEqualTo("Id_Viaje", tripId);
            ParseQuery<ParseObject> tripCoordQuery = ParseQuery.getQuery("Viaje");
            tripCoordQuery.whereEqualTo("objectId", tripId);

            try {
                String cityId = tripCoordQuery.getFirst().getString("Id_Ciudad");
                ParseQuery<ParseObject> cityCoordQuery = ParseQuery.getQuery("Ciudad");
                cityCoordQuery.whereEqualTo("objectId", cityId);
                Double latitude = cityCoordQuery.getFirst().getDouble("Latitud");
                Double longitude = cityCoordQuery.getFirst().getDouble("Longitud");

                ArrayList<String> sitesLatitudeArray = new ArrayList<String>();
                ArrayList<String> sitesLongitudeArray = new ArrayList<String>();
                ArrayList<String> sitesNameArray = new ArrayList<String>();

                List<ParseObject> sites = siteCoordQuery.find();

                for(int i = 0; i < sites.size(); i++) { //tots els llocs
                    Double siteLatitude = sites.get(i).getDouble("Latitud");
                    Double siteLongitude = sites.get(i).getDouble("Longitud");
                    String siteName = sites.get(i).getString("Nombre");
                    sitesLatitudeArray.add(siteLatitude.toString());
                    sitesLongitudeArray.add(siteLongitude.toString());
                    sitesNameArray.add(siteName.toString());
                }

                //enviem el viatge i la ubicació per fer focus
                Bundle paramsMaps = new Bundle();
                paramsMaps.putString("route", "true"); //mirem la ruta
                paramsMaps.putStringArrayList("latitudeArray", sitesLatitudeArray);
                paramsMaps.putStringArrayList("longitudeArray", sitesLongitudeArray);
                paramsMaps.putStringArrayList("nameArray", sitesNameArray);
                //per focus
                paramsMaps.putString("tripId", tripId);
                paramsMaps.putDouble("latitude", latitude);
                paramsMaps.putDouble("longitude", longitude);
                paramsMaps.putSerializable("userObject", myUser);
                paramsMaps.putString("nombre_viaje", nombreViaje);

                Intent siteMaps = new Intent(this, SiteMapsActivity.class);
                siteMaps.putExtras(paramsMaps);
                startActivity(siteMaps);

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent (SiteList.this, TripList.class);
        intent.putExtra("id_viaje", idViaje);
        intent.putExtra("nombre_viaje", nombreViaje);
        intent.putExtra("myUser", myUser);
        startActivity(intent);
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