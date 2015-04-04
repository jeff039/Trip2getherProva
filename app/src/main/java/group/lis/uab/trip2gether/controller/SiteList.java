
package group.lis.uab.trip2gether.controller;

        import android.content.Intent;
        import android.graphics.Color;
        import android.graphics.drawable.ColorDrawable;
        import android.support.v4.widget.DrawerLayout;
        import android.support.v7.app.ActionBar;
        import android.support.v7.app.ActionBarActivity;
        import android.os.Bundle;
        import android.view.Gravity;
        import android.view.LayoutInflater;
        import android.view.Menu;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.ArrayAdapter;
        import android.widget.Button;
        import android.widget.ImageButton;
        import android.widget.ListView;
        import android.widget.TextView;
        import android.support.v4.app.Fragment;
        import android.view.MenuItem;

        import com.google.android.gms.maps.model.LatLng;
        import com.parse.ParseCloud;
        import com.parse.ParseException;
        import com.parse.ParseObject;
        import com.parse.ParseQuery;

        import org.json.JSONObject;

        import java.lang.reflect.Array;
        import java.util.ArrayList;
        import java.util.HashMap;
        import java.util.List;

        import group.lis.uab.trip2gether.R;
        import group.lis.uab.trip2gether.model.DrawerItemClickListener;

/**
 * Created by Mireia on 25/03/2015.
 */
public class SiteList  extends ActionBarActivity {

    private static Intent intent = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site_list);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
        this.setSupportBar();
        this.initializeDrawerLayout();
        this.initializeButtons();
    }

    /**
     * Elements de la interfície
     */
    public void initializeButtons() {
        ImageButton openDrawer = (ImageButton) findViewById(R.id.openDrawer);
        openDrawer.setOnClickListener(clickDrawer);
    }

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
     * Drawer layout
     */
    public void initializeDrawerLayout(){
        DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        //no cal fer un adaptador a la mDrawer,
        //ja est� configurat en la situaci� dels elements al xml
        ListView mDrawerList = (ListView) findViewById(R.id.left_drawer);
        //per� s� en la ListView:
        //agafem les opcions de "strings"
        String [] options = getResources().getStringArray(R.array.options_array);
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, options));
        //simple_list_itm_1, �s un layout "prefabricat" que ve amb la API,
        //consistent en un  layout amb un text simple que requereix el ArrayAdapter
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
        getMenuInflater().inflate(R.menu.menu_site_list, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == R.id.addSite)
        {
            //rebem el viatge en que estem
            //Bundle trip = getIntent().getExtras();
            //String tripId = trip.getString("tripId");

            //HARDCODED
            //PROVISIONAL (TESTBARCELONA)
            String tripId = "36IJhdT4rp";

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

                Intent siteMaps = new Intent(this, SiteMapsActivity.class);
                siteMaps.putExtras(paramsMaps);
                startActivity(siteMaps);


            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        if(id == R.id.mapRoute)
        {
            //rebem el viatge en que estem
            //Bundle trip = getIntent().getExtras();
            //String tripId = trip.getString("tripId");

            //HARDCODED
            //PROVISIONAL (TESTBARCELONA)
            String tripId = "36IJhdT4rp";
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

                for(int i = 0; i < sites.size(); i++) //tots els llocs
                {
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

                Intent siteMaps = new Intent(this, SiteMapsActivity.class);
                siteMaps.putExtras(paramsMaps);
                startActivity(siteMaps);

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    public void setSupportBar(){
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.rgb(75, 74, 104)));
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar_site_list);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_site_list, container, false);

            if(intent!=null){
                String nombre = intent.getStringExtra("nombre");
                String descripcion = intent.getStringExtra("descripcion");
                String duracion = intent.getStringExtra("duracion");
                String puntuacion_Total= intent.getStringExtra("puntuacion_Total");


                TextView nombreSitio = (TextView) rootView.findViewById(R.id.nombreSitio);
                nombreSitio.setText(nombre);
            }

            return rootView;
        }
    }

}
