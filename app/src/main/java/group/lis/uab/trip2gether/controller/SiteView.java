package group.lis.uab.trip2gether.controller;

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
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.Parse;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;

import java.util.HashMap;
import java.util.List;

import group.lis.uab.trip2gether.R;
import group.lis.uab.trip2gether.Resources.Utils;
import group.lis.uab.trip2gether.model.Site;
import group.lis.uab.trip2gether.model.User;

/**
 * Created by Mireia on 04/04/2015.
 */
public class SiteView  extends ActionBarActivity {

    private String currentSiteId;
    private String currentSiteNombre;
    private String nombreViaje;
    public static Site currentSiteRefresh = null;
    private User myUser;
    private String idViaje;
    private String idViajeRefresh;
    private GoogleMap mMap;
    private double latitude;
    private double longitude;
    private Toolbar mToolbar;
    private ListView leftDrawerList;
    private Intent intent;
    private RatingBar ratingBar;
    public static Boolean refreshActivity = false;
    private List<ParseObject> idsSitio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site_view);
        Intent intent = getIntent();
        currentSiteId = intent.getStringExtra("currentSiteId");
        nombreViaje = intent.getStringExtra("nombre_viaje");
        myUser = (User)intent.getSerializableExtra("myUser");
        idViaje= intent.getStringExtra("id_viaje");

        mToolbar = (Toolbar) findViewById(R.id.action_bar_site_view);
        setSupportActionBar(mToolbar);
        try {
            idsSitio = Utils.getRegistersFromBBDD(currentSiteId, "Puntuacion", "Id_Sitio");
        } catch (ParseException e) {
            e.printStackTrace();
        }

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
        String siteId = currentSiteId;

        ParseObject site= null;
        site = Utils.getRegistersFromBBDD(siteId, "Sitio", "objectId").get(0);
        this.currentSiteNombre= site.getString("Nombre");

        TextView name = (TextView)findViewById(R.id.nombreSiteView);
        name.setText(site.getString("Nombre"));

        TextView description = (TextView)findViewById(R.id.descripcionSiteView);
        description.setText(site.getString("Descripcion"));

        TextView duration = (TextView)findViewById(R.id.duracionSiteView);
        duration.setText(Double.toString(site.getDouble("Duracion")));

        TextView price = (TextView)findViewById(R.id.precioSiteView);
        price.setText(Double.toString(site.getDouble("Precio")));

        latitude = site.getDouble("Latitud");
        longitude = site.getDouble("Longitud");

        TextView rateValue = (TextView)findViewById(R.id.rateValue);
        Double a = 0.0;
        List<ParseObject> puntuaciones = Utils.getRegistersFromBBDD(siteId, "Puntuacion", "Id_Sitio");

        if(!puntuaciones.isEmpty()) {
            for (int i = 0; i < puntuaciones.size(); i++) {
                if (myUser.getObjectId().equals(puntuaciones.get(i).getString("Id_Usuario"))) {
                    a = puntuaciones.get(i).getDouble("Estrellas");
                }
            }
        }
        rateValue.setText(a.toString());
        a = site.getDouble("Estrellas");
        ratingBar.setRating(a.longValue());
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
            Double promedio;
            Double sumatorio;
            int contador = 0;
            sumatorio = 0.0;
            for(int i=0;i<idsSitio.size(); i++){
                ParseObject idSitio = idsSitio.get(i);
                if(idSitio.getString("Id_Usuario").equals(myUser.getObjectId())){
                    rateExist = true;
                }else{
                    //sumamos todos los votos que no son el nuestro.
                    sumatorio = sumatorio + idSitio.getDouble("Estrellas");
                    contador++;
                }
            }

            //actualizamos nuestro voto
            if(rateExist) {
                HashMap<String, Object> params = new HashMap<String, Object>();
                params.put("estrellas", ratingBar.getRating());
                params.put("id_sitio", currentSiteId);
                params.put("id_usuario", myUser.getObjectId());
                try {
                    String ar = ParseCloud.callFunction("updateRate", params);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            //o añadimos nuestro voto
            else{
                HashMap<String, Object> params = new HashMap<String, Object>();
                params.put("estrellas", ratingBar.getRating());
                params.put("id_sitio", currentSiteId);
                params.put("id_usuario", myUser.getObjectId());
                try {
                    List<ParseObject> ar = ParseCloud.callFunction("addRate", params);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            //Actualizar campo Estrellas de tabla Sitio.
            //primero le sumamos nuestro voto y despues hacemos el promedio.
            sumatorio= sumatorio + ratingBar.getRating();
            contador = contador + 1;
            promedio = sumatorio / contador;
            try {
                Utils.setValueBBDD(promedio, "Sitio", "Estrellas", currentSiteId);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            Intent refresh = new Intent (SiteView.this, SiteView.class);
            refresh.putExtra("currentSiteId", currentSiteId);
            refresh.putExtra("myUser", myUser);
            refresh.putExtra("id_viaje", idViaje);
            refresh.putExtra("nombre_viaje", nombreViaje);
            startActivity(refresh);
        }
    };


    public Button.OnClickListener clickEditThisSite = new Button.OnClickListener() {
        public void onClick(View v) {
            Intent intent = new Intent(SiteView.this, EditSiteForm.class);
            intent.putExtra("mySiteId", currentSiteId);
            intent.putExtra("myUser", myUser);
            intent.putExtra("id_viaje", idViaje);
            intent.putExtra("nombre_viaje", nombreViaje);
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
            case (R.id.EditThisSite):
                Intent editThisSite = new Intent(this, EditSiteForm.class);
                editThisSite.putExtra("mySiteId", currentSiteId);
                editThisSite.putExtra("myUser", myUser);
                editThisSite.putExtra("id_viaje", idViaje);
                editThisSite.putExtra("nombre_viaje", nombreViaje);
                startActivity(editThisSite);
                return true;
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
                .title(this.currentSiteNombre).draggable(true));
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

    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent (SiteView.this, SiteList.class);
        intent.putExtra("id_viaje", idViaje);
        intent.putExtra("nombre_viaje", nombreViaje);
        intent.putExtra("myUser", myUser);
        startActivity(intent);
    }
}