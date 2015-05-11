package group.lis.uab.trip2gether.controller;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import group.lis.uab.trip2gether.R;
import group.lis.uab.trip2gether.Resources.Utils;
import group.lis.uab.trip2gether.model.Site;
import group.lis.uab.trip2gether.model.User;

public class SiteMapsActivity extends FragmentActivity implements GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    MarkerOptions marker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site_maps);
        setUpMapIfNeeded();
    }

    public void setUpInfoWindow(final Site site)
    {
        /////////////////INFO WINDOW/////////
        // Getting reference to the SupportMapFragment of activity_main.xml
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        // Getting GoogleMap object from the fragment
        mMap = mapFragment.getMap();

        // Setting a custom info window adapter for the google map
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            // Use default InfoWindow frame
            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            // Defines the contents of the InfoWindow
            @Override
            public View getInfoContents(final Marker marker) {

                // Getting view from the layout file info_window_layout
                View infoWindow = getLayoutInflater().inflate(R.layout.maps_title, null);
                ImageView siteImage = (ImageView) infoWindow.findViewById(R.id.siteImage);

                //CONTINGUT DE LA IMATGE
                Site clickedSite = site;
                String siteId = clickedSite.getId();
                ParseObject site= null;
                try {
                    site = Utils.getRegistersFromBBDD(siteId, "Sitio", "objectId").get(0);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                ParseFile imagen = site.getParseFile("Imagen");
                if (imagen != null) {
                    Utils.setImageViewWithParseFile(siteImage, imagen, false);
                }
                // Returning the view containing InfoWindow contents
                return infoWindow;
            }
        });

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Bundle paramsIn = getIntent().getExtras();
                User user = (User) paramsIn.getSerializable("userObject");
                Site site = getClickedSite(marker);
                //////////////////////////////////////////////////////////
                Bundle paramsOut = new Bundle();
                paramsOut.putString("currentSiteId", site.getId());
                paramsOut.putSerializable("myUser", user);
                Intent siteView = new Intent(SiteMapsActivity.this, SiteView.class);
                siteView.putExtras(paramsOut);
                startActivity(siteView);
            }
        });
    }



    public Site getClickedSite(Marker marker)
    {
        //////////////////////////////////
        LatLng position = marker.getPosition();
        ///////QUERY SITE/////////////////////
        HashMap<String, Object> paramsQuery = new HashMap<String, Object>();
        paramsQuery.put("latitude", position.latitude);
        paramsQuery.put("longitude", position.longitude);

        List<ParseObject> siteResponse = null; //crida al BE
        try {
            siteResponse = ParseCloud.callFunction("getSiteByCoordenates", paramsQuery);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        ParseObject siteParse = siteResponse.get(0);
        Site site = new Site(siteParse.getString("Nombre"), siteParse.getString("Descripcion"),
                siteParse.getParseFile("Imagen"), siteParse.getString("Id_Viaje"), siteParse.getObjectId(),
                siteParse.getDouble("Duracion"),
                siteParse.getDouble("Precio"), siteParse.getDouble("Latitud"), siteParse.getDouble("Longitud"));
        return  site;
    }

    /////BOTÓ OK/////////////
    public void initializeButtonsAdd()
    {
        Button ok = (Button) findViewById(R.id.maps_ok);
        ok.setOnClickListener(clickOKAdd);
    }

    public void initializeButtonsRoute()
    {
        Button ok = (Button) findViewById(R.id.maps_ok);
        ok.setOnClickListener(clickOKRoute);
    }

    public Button.OnClickListener clickOKAdd = new Button.OnClickListener() {
        public void onClick(View v) {
            if(marker != null) { //si hem marcat
                //cridem el formulari (pas2)
                Bundle params = getIntent().getExtras();
                String tripId = params.getString("tripId");
                User myUser = (User) params.getSerializable("myUser");
                String nombreViaje = params.getString("nombre_viaje");

                //enviem el punt al formulari si l'hem marcat
                Bundle paramsSiteForm = new Bundle();
                paramsSiteForm.putString("tripId", tripId);
                paramsSiteForm.putDouble("latitude", marker.getPosition().latitude);
                paramsSiteForm.putDouble("longitude", marker.getPosition().longitude);

                paramsSiteForm.putSerializable("myUser", myUser);
                paramsSiteForm.putString("id_viaje", tripId);
                paramsSiteForm.putString("nombre_viaje", nombreViaje);
                //cridem el formulari
                Intent newSite = new Intent(SiteMapsActivity.this, NewSiteForm.class);
                newSite.putExtras(paramsSiteForm);
                startActivity(newSite);
            }
            else
            {
                String alertString = getResources().getString(R.string.anyLocation); //missatge de alerta
                //Enviem el missatge dient que 's'ha inserit correctament
                new AlertDialog.Builder(SiteMapsActivity.this) //ens trobem en una funció de un botó, especifiquem la classe (no this)
                        //.setTitle("DB")
                        .setMessage(alertString)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //no fem res
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .show();
            }
        }
    };

    public Button.OnClickListener clickOKRoute = new Button.OnClickListener() {
        public void onClick(View v) {
            SiteMapsActivity.super.onBackPressed(); //tornem enrere
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        Bundle params = getIntent().getExtras();

        if(params.getString("route").equals("false")) { //afegint site
            //SI ESTEM EDITANT UN PUNT
            double latitutde = params.getDouble("latitude");
            double longitude = params.getDouble("longitude");
            ////////////////////////////////////
            CameraUpdate center =
                    CameraUpdateFactory.newLatLng(new LatLng(latitutde, longitude));
            CameraUpdate zoom = CameraUpdateFactory.zoomTo(12);
            mMap.moveCamera(center);
            mMap.animateCamera(zoom);

            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

                @Override
                public void onMapClick(LatLng point) {
                    mMap.clear(); //eliminem els markers anteriors
                    String positionText = point.toString();
                    marker = new MarkerOptions()
                            .position(new LatLng(point.latitude, point.longitude));
                    mMap.addMarker(marker); //nou marker
                    //System.out.println(point.latitude + "---" + point.longitude);
                }
            });

            this.initializeButtonsAdd();
        }
        else if (params.getString("route").equals("true")) //mirant la ruta
        {
            //què passarà quan pitjem en una marca
            mMap.setOnMarkerClickListener(this);

            double latitutde = params.getDouble("latitude");
            double longitude = params.getDouble("longitude");
            ////////////////////////////////////
            CameraUpdate center =
                    CameraUpdateFactory.newLatLng(new LatLng(latitutde, longitude));
            CameraUpdate zoom = CameraUpdateFactory.zoomTo(12);
            mMap.moveCamera(center);
            mMap.animateCamera(zoom);

            ArrayList<String> sitesLatitude = new ArrayList<String>();
            ArrayList<String> sitesLongitude = new ArrayList<String>();
            ArrayList<String> sitesName = new ArrayList<String>();

            sitesLatitude = params.getStringArrayList("latitudeArray");
            sitesLongitude = params.getStringArrayList("longitudeArray");
            sitesName = params.getStringArrayList("nameArray");

            for(int i = 0; i < sitesLatitude.size(); i++) //afegim els punts
            {
                mMap.addMarker(new MarkerOptions().position(
                        new LatLng(Double.parseDouble(sitesLatitude.get(i)), Double.parseDouble(sitesLongitude.get(i))))
                        .title(sitesName.get(i)).draggable(true));
                //no cal internacionalització per els noms, els afegeix el propi usuari
            }

            this.initializeButtonsRoute();

        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Site site = getClickedSite(marker);
        setUpInfoWindow(site);
        marker.showInfoWindow();
        return false;
    }
}
