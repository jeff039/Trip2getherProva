package group.lis.uab.trip2gether.controller;

import android.content.Intent;
import android.os.Bundle;;
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
import android.widget.ListView;
import android.widget.TextView;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import group.lis.uab.trip2gether.R;
import group.lis.uab.trip2gether.model.Site;
import group.lis.uab.trip2gether.model.User;

/**
 * Created by Mireia on 04/04/2015.
 */
public class SiteView  extends ActionBarActivity {

    private Site currentSite;
    private User myUser;
    private GoogleMap mMap;
    private double latitude;
    private double longitude;
    private Toolbar mToolbar;
    private ListView leftDrawerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site_view);
        Intent intent = getIntent();
        currentSite = (Site) intent.getSerializableExtra("currentSite"); //serialització de l'objecte

        mToolbar = (Toolbar) findViewById(R.id.action_bar_site_view);
        setSupportActionBar(mToolbar);

        this.initializeDrawerLayout();
        this.initializeButtons();
        try {
            this.initializeSiteData();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        setUpMapIfNeeded();
    }

    /**
     * Method initializeSiteData. Interfície
     * @throws ParseException
     */
    public void initializeSiteData() throws ParseException {
        String siteId = currentSite.getId();

        TextView name = (TextView)findViewById(R.id.nombreSiteView);
        name.setText(currentSite.getNombre());

        TextView description = (TextView)findViewById(R.id.descripcionSiteView);
        description.setText(currentSite.getDescripcion());

        TextView duration = (TextView)findViewById(R.id.duracionSiteView);
        duration.setText(Double.toString(currentSite.getDuracion()));

        TextView price = (TextView)findViewById(R.id.precioSiteView);
        price.setText(Double.toString(currentSite.getPrecio()));

        ParseQuery<ParseObject> siteCoordQuery = ParseQuery.getQuery("Sitio");
        siteCoordQuery.whereEqualTo("objectId", siteId);
        latitude = siteCoordQuery.getFirst().getDouble("Latitud");
        longitude = siteCoordQuery.getFirst().getDouble("Longitud");
        //TODO change last 4 lines with next
        /*
        latitude = currentSite.getLatitud();
        longitude = currentSite.getLongitud();
        */
    }
    /**
     * Method initializeButtons. Elements de la interfície
     */
    public void initializeButtons(){
        ImageButton openDrawer = (ImageButton) findViewById(R.id.openDrawer);
        openDrawer.setOnClickListener(clickDrawer);

        ImageButton openEditThisSite = (ImageButton) findViewById(R.id.EditThisSite);
        openEditThisSite.setOnClickListener(clickEditThisSite);
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
    public Button.OnClickListener clickEditThisSite = new Button.OnClickListener() {
        public void onClick(View v) {
            Intent intent = new Intent(SiteView.this, EditSiteForm.class);
            intent.putExtra("mySite", currentSite);
            startActivity(intent);
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

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            switch (position){
                case 0:	openMyProfile();
                    break;
            }
        }
    }

    /**
     * Method openMyProfile.
     */
    public void openMyProfile() {
        Intent userProfile = new Intent(this, UserProfile.class);
        userProfile.putExtra("myUser", this.myUser);
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
        getMenuInflater().inflate(R.menu.menu_site_view, menu);
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
            /*case (R.id.edit_site):
                //FALTA IMPLEMENTAR
                return true;*/
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Method setUpMap.
     */
    private void setUpMap() {
        CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(latitude, longitude));
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(12);
        mMap.moveCamera(center);
        mMap.animateCamera(zoom);
        mMap.addMarker(new MarkerOptions().position(
                new LatLng(latitude, longitude))
                .title(currentSite.getNombre()).draggable(true));
    }

    /**
     * Medthod setUpMapIfNeeded
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapSite))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }
}