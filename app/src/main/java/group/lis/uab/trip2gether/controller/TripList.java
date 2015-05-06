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
import group.lis.uab.trip2gether.R;
import group.lis.uab.trip2gether.Resources.Utils;
import group.lis.uab.trip2gether.model.Trip;
import group.lis.uab.trip2gether.model.TripListAdapter;
import group.lis.uab.trip2gether.model.User;

public class TripList extends ActionBarActivity {

    protected ListAdapter adapter;
    protected ListView lista;
    private static Context context;
    private static Intent intent;
    private User myUser;
    private ArrayList<Trip> trips = new ArrayList<Trip>();
    private Toolbar mToolbar;
    private ListView leftDrawerList;
    int ICONS[] = {R.drawable.ic_place_grey600_18dp,R.drawable.ic_place_grey600_18dp,R.drawable.ic_place_grey600_18dp,R.drawable.ic_place_grey600_18dp};

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

        mToolbar = (Toolbar) findViewById(R.id.action_bar_trip_list);
        setSupportActionBar(mToolbar);

        this.initializeDrawerLayout();
        this.initializeButtons();
        myUser = (User) intent.getSerializableExtra("myUser");
        this.checkNotifications(); //per canviar el botó


        try {
            this.ViewTripFromBBDD();
        } catch (com.parse.ParseException e) {
            e.printStackTrace();
        }
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
            openDrawer.setImageResource(R.drawable.ic_action_noti);
        }
        else //si no tenim noves notificacions
        {
            openDrawer.setImageResource(R.drawable.ic_menu_white_18dp);
        }
    }

    /**
     * Method ViewTripFromBBDD. Métode per visualitzar els viatjes associats a un usuari en la llista TripList
     * @throws com.parse.ParseException
     */
    public void ViewTripFromBBDD() throws com.parse.ParseException {
        List<ParseObject> idsViatje = Utils.getRegistersFromBBDD(myUser.getObjectId(), "Grupo", "Id_Usuario");

        if(!idsViatje.isEmpty()) {
            for(int i=0;i<idsViatje.size();i++){
                //ParseObject viatjeId = idsViatje.get(i);
                String idViaje = idsViatje.get(i).getString("Id_Viaje");

                List<ParseObject> getId = Utils.getRegistersFromBBDD(idViaje, "Viaje", "objectId");
                ParseObject camposViaje = getId.iterator().next();

                String idCiudad = camposViaje.getString("Id_Ciudad");
                List<ParseObject> datosIdCiudad = Utils.getRegistersFromBBDD(idCiudad, "Ciudad", "objectId");
                String pais;
                String ciudad;

                if(datosIdCiudad.size()==0){

                    pais="España";
                    ciudad="Barcelona";

                }else{
                    pais = datosIdCiudad.get(0).getString("Pais");
                    ciudad = datosIdCiudad.get(0).getString("Nombre");
                }

                Trip trip = new Trip(camposViaje.getString("Nombre"), pais,
                        ciudad, camposViaje.getDate("Fecha_Inicial"),
                        camposViaje.getDate("Fecha_Final"), camposViaje.getParseFile("Imagen"));
                /*
                Trip trip = new Trip(camposViaje.getString("Nombre"), pais,
                        ciudad, camposViaje.getDate("Fecha_Inicial"),
                        camposViaje.getDate("Fecha_Final"), camposViaje.getParseFile("Imagen").getUrl());
                */
                trip.setId(idViaje);
                trips.add(trip);
            }
        }

        TripListAdapter adaptador = new TripListAdapter(context, R.layout.trip_list_item_row, trips, myUser);
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
        String [] options = getResources().getStringArray(R.array.options_array);
        leftDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, options));
        leftDrawerList.setOnItemClickListener(new DrawerItemClickListener());
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
            DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
            switch (position){
                case 1:
                    openMyProfile();
                    mDrawerLayout.closeDrawer(Gravity.LEFT);
                    break;
                case 2:
                    openMyTrips();
                    break;
                case 3:
                    openFriends();
                    mDrawerLayout.closeDrawer(Gravity.LEFT);
                    break;
                case 4:
                    //el botó  de notificacions es canviara si a la bd canvia
                    openNotificationList();
                    mDrawerLayout.closeDrawer(leftDrawerList);
                    break;
                case 5: //ajustes
                    openSettings();
                    break;
                case 6: //cerrar sesión
                    logout();
                    break;
            }
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


    /**
     * Method onCreateOptionsMenu. Barra superior
     * @param menu
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_trip_list, menu);
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
            case (R.id.addTrip):
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