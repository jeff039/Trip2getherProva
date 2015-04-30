package group.lis.uab.trip2gether.controller;

import android.app.PendingIntent;
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
import android.widget.RatingBar;
import android.widget.TextView;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;

import group.lis.uab.trip2gether.R;
import group.lis.uab.trip2gether.model.Site;
import group.lis.uab.trip2gether.model.User;

/**
 * Created by Mireia on 04/04/2015.
 */
public class SiteView  extends ActionBarActivity {

    private Site currentSite;
    public static Site currentSiteRefresh = null;
    private User myUser;
    public static User myUserRefresh = null;
    private GoogleMap mMap;
    private double latitude;
    private double longitude;
    private Toolbar mToolbar;
    private ListView leftDrawerList;
    private String siteId;
    private Intent intent;
    private RatingBar ratingBar;
    public static Boolean refreshActivity = false;
    private List<ParseObject> idsSitio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site_view);
        Intent intent = getIntent();
        if(refreshActivity){
            currentSite = currentSiteRefresh;
            myUser = myUserRefresh;
            refreshActivity = false;
        }
        else {
            currentSite = (Site) intent.getSerializableExtra("currentSite"); //serialització de l'objecte
            myUser = (User) intent.getSerializableExtra("myUser");
        }

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

        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        Button btnSubmit = (Button) findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(rateButton);

        try {
            initRate();
        } catch (ParseException e) {
            e.printStackTrace();
        }

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

    public Button.OnClickListener rateButton = new Button.OnClickListener() {
        public void onClick(View v) {
            Boolean rateExist = false;
            for(int i=0;i<idsSitio.size(); i++){
                ParseObject idSitio = idsSitio.get(i);
                if(idSitio.getString("Id_Usuario").equals(myUser.getObjectId().toString())){
                    rateExist = true;
                }
            }

            if(rateExist) {
                HashMap<String, Object> params = new HashMap<String, Object>();
                params.put("estrellas", ratingBar.getRating());
                params.put("id_sitio", currentSite.getId());
                params.put("id_usuario", myUser.getObjectId());
                try {
                    String ar = ParseCloud.callFunction("updateRate", params);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            else{
                HashMap<String, Object> params = new HashMap<String, Object>();
                params.put("estrellas", ratingBar.getRating());
                params.put("id_sitio", currentSite.getId());
                params.put("id_usuario", myUser.getObjectId());
                try {
                    List<ParseObject> ar = ParseCloud.callFunction("addRate", params);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            refreshActivity = true;
            myUserRefresh = myUser;
            currentSiteRefresh = currentSite;
            Intent refresh = new Intent (SiteView.this, SiteView.class);
            refresh.putExtra("currentSite", currentSite.getId());
            refresh.putExtra("myUser", myUser);
            startActivity(refresh);
            //PendingIntent pi = PendingIntent.getActivity(this, 0, intent, Intent.FLAG_ONE_SHOT);
            finish();
            /**
             intent.putExtra("currentSite", currentSite.getId());
             intent.putExtra("myUser", myUser.getObjectId());
             finish();
             startActivity(intent);**/
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
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
            switch (position){
                case 1:
                    openMyProfile();
                    mDrawerLayout.closeDrawer(Gravity.LEFT);
                    break;
                case 2:
                    openMyTripList();
                    mDrawerLayout.closeDrawer(Gravity.LEFT);
                    break;
                case 4:
                    openNotificationList();
                    mDrawerLayout.closeDrawer(Gravity.LEFT);
                    break;
            }
        }
    }

    /**
     * Method openMyProfile.
     */
    public void openMyProfile() {
        Intent userProfile = new Intent(this, UserProfile.class);
        userProfile.putExtra("myUser", myUser);
        startActivity(userProfile);
    }

    public void openNotificationList() {
        Intent notificationList = new Intent(this, NotificationList.class);
        notificationList.putExtra("myUser", myUser);
        startActivity(notificationList);
    }

    public void openMyTripList() {
        Intent tripList = new Intent(this, TripList.class);
        tripList.putExtra("myUser", myUser);
        startActivity(tripList);
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

    public List<ParseObject> getValueBBDD(String valueFieldTable, String table, String field) throws com.parse.ParseException {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("valueFieldTable", valueFieldTable);
        params.put("table", table);
        params.put("field", field);
        return ParseCloud.callFunction("getId", params);
    }

    public Boolean initRate() throws ParseException {
        idsSitio = getValueBBDD(currentSite.getId(), "Puntuacion", "Id_Sitio");

        Double sumatorio;
        int contador = 0;
        sumatorio = 0.0;

        TextView rateValue = (TextView)findViewById(R.id.rateValue);

        if(idsSitio.size()== 0){
            HashMap<String, Object> params = new HashMap<String, Object>();
            params.put("estrellas", 0);
            params.put("id_sitio", currentSite.getId());
            params.put("id_usuario", myUser.getObjectId());
            String ar = ParseCloud.callFunction("addRate", params);
            if(ar.isEmpty()){
                return false;
            }


            rateValue.setText(0);

            ratingBar.setRating(0);
            return true;
        }

        for(int i=0;i<idsSitio.size(); i++){
            ParseObject idSitio = idsSitio.get(i);
            sumatorio = sumatorio + idSitio.getDouble("Estrellas");
            contador++;
        }

        sumatorio = sumatorio / contador;
        DecimalFormat df = new DecimalFormat("0.00");
        String finalRate = df.format(sumatorio);
        rateValue.setText(finalRate);
        ratingBar.setRating(sumatorio.longValue());

        return true;
    }
}