package group.lis.uab.trip2gether.controller;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import group.lis.uab.trip2gether.R;

public class SiteMapsActivity extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    MarkerOptions marker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site_maps);
        setUpMapIfNeeded();
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
                //enviem el punt al formulari si l'hem marcat
                Bundle paramsSiteForm = new Bundle();
                paramsSiteForm.putString("tripId", tripId);
                paramsSiteForm.putDouble("latitude", marker.getPosition().latitude);
                paramsSiteForm.putDouble("logitude", marker.getPosition().longitude);
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
                            .position(new LatLng(point.latitude, point.longitude))
                            .title(positionText);
                    mMap.addMarker(marker); //nou marker
                    //System.out.println(point.latitude + "---" + point.longitude);
                }
            });

            this.initializeButtonsAdd();
        }
        else if (params.getString("route").equals("true")) //mirant la ruta
        {
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
}
