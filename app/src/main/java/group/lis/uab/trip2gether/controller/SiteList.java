package group.lis.uab.trip2gether.controller;

import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.view.MenuItem;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import java.util.ArrayList;
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
        String nombreViaje = intent.getStringExtra("nombre_viaje");

        mToolbar = (Toolbar) findViewById(R.id.action_bar_site_list);
        setSupportActionBar(mToolbar);

        TextView name = (TextView)findViewById(R.id.txtSiteName);
        name.setText(nombreViaje);

        myUser = (User) intent.getSerializableExtra("myUser");

        this.initializeDrawerLayout();
        this.initializeButtons();

        try {
            this.ViewSiteFromBBDD();
        } catch (com.parse.ParseException e) {
            e.printStackTrace();
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
            this.sites.add(sitio);
        }

        SiteListAdapter adaptador = new SiteListAdapter(context,R.layout.site_list_item_row, sites);
        lista = (ListView)findViewById(R.id.listaSitios);
        lista.setAdapter(adaptador);

        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent (SiteList.this, SiteView.class);
                intent.putExtra("currentSite", sites.get(position));
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

            Intent newSite = new Intent(this, NewSiteForm.class);
            newSite.putExtra("id_viaje", this.idViaje);
            startActivity(newSite);

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

                Intent siteMaps = new Intent(this, SiteMapsActivity.class);
                siteMaps.putExtras(paramsMaps);
                startActivity(siteMaps);

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return super.onOptionsItemSelected(item);
    }
}