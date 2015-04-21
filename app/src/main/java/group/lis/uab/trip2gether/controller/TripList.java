package group.lis.uab.trip2gether.controller;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import com.parse.ParseObject;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import group.lis.uab.trip2gether.R;
import group.lis.uab.trip2gether.model.Trip;
import group.lis.uab.trip2gether.model.TripListAdapter;
import group.lis.uab.trip2gether.model.User;

public class TripList extends ActionBarActivity {

    protected Cursor cursor;
    protected ListAdapter adapter;
    protected ListView lista;
    private static Context context = null;
    private static Intent intent = null;
    private User myUser;
    private ArrayList<Trip> trips = new ArrayList<Trip>();
    private ArrayList<String> tripsNoms = new ArrayList<String>();
    private Toolbar mToolbar;
    private ListView leftDrawerList;

    private TripList data[] = null;

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
        try {
            this.ViewTripFromBBDD();
        } catch (com.parse.ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method getIdsBBDD. Métode genéric per obtenir una llista de valors d'un camp que pertany a una entitat de la BBDD
     * @param valueFieldTable Valor del <field> de la <table>
     * @param table Taula de la BBDD
     * @param field Camp de la <table>
     * @return List<ParseObject>
     * @throws com.parse.ParseException
     */
    public List<ParseObject> getValueBBDD(String valueFieldTable, String table, String field) throws com.parse.ParseException {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("valueFieldTable", valueFieldTable);
        params.put("table", table);
        params.put("field", field);
        return ParseCloud.callFunction("getId", params);
    }

    /**
     * Method ViewTripFromBBDD. Métode per visualitzar els viatjes associats a un usuari en la llista TripList
     * @throws com.parse.ParseException
     */
    public void ViewTripFromBBDD() throws com.parse.ParseException {
        List<ParseObject> idsViatje = getValueBBDD(myUser.getObjectId(), "Grupo", "Id_Usuario");

        if(!idsViatje.isEmpty()) {
            for(int i=0;i<idsViatje.size();i++){
                ParseObject viatjeId = idsViatje.get(i);
                String idViaje = viatjeId.getString("Id_Viaje");

                List<ParseObject> getId = getValueBBDD(idViaje, "Viaje", "objectId");
                ParseObject camposViaje = getId.iterator().next();

                //TODO OJO que peta

                String idCiudad = camposViaje.getString("Id_Ciudad");
                List<ParseObject> datosIdCiudad = getValueBBDD(idCiudad, "Ciudad", "objectId");
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
                trip.setId(idViaje);
                trips.add(trip);
                tripsNoms.add(trip.getNombre());
            }
        }

        TripListAdapter adaptador = new TripListAdapter(context, R.layout.trip_list_item_row, trips, myUser);
        lista = (ListView)findViewById(R.id.listaViajes);
        lista.setAdapter(adaptador);

        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent (TripList.this, SiteList.class);
                Trip a = trips.get(position);
                intent.putExtra("id_viaje", a.getId());
                intent.putExtra("nombre_viaje", a.getNombre());
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
        leftDrawerList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, options));
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
            switch (position){
                case 0:	openMyProfile();
                    break;
                case 1: openMyProfile();
                    break;
            }
        }
    }

    /**
     * Method openMyProfile
     */
    public void openMyProfile() {
        Intent userProfile = new Intent(this, UserProfile.class);
        userProfile.putExtra("myUser", myUser);
        startActivity(userProfile);
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
}